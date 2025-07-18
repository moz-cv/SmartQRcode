package com.szr.co.smart.qr.service

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.szr.co.smart.qr.BuildConfig
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.activity.StartActivity
import com.szr.co.smart.qr.event.AppEvent
import com.szr.co.smart.qr.model.PushModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.apply
import kotlin.collections.forEach
import kotlin.jvm.java
import kotlin.let
import kotlin.text.isNullOrEmpty

class FireMessagePushLogic {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { FireMessagePushLogic() }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_USER_PRESENT) {
                SmartApp.instance.scope.launch {
                    delay(1000)
                    msgList?.let {
                        if (BuildConfig.DEBUG) Log.d("TAG_FCM_MSG", "亮屏发送")
                        withContext(Dispatchers.Main) {
                            sendNotify(it)
                            msgList = null
                        }

                    }
                }
            }
        }
    }

    init {
        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            SmartApp.instance.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            SmartApp.instance.registerReceiver(receiver, filter)
        }

        // 创建通知渠道（Android 8.0 及以上需要）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "push_message",
                "Smart QRCode",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for push message"
            }
            NotificationManagerCompat.from(SmartApp.instance)
                .createNotificationChannel(channel)
        }
    }


    private var msgList: List<PushModel>? = null


    private suspend fun requestBitMap(url: String?): Bitmap? {
        return withContext(Dispatchers.IO) {
            if (url.isNullOrEmpty()) return@withContext null
            try {
                // 使用 Glide 的 await 扩展函数进行异步加载
                Glide.with(SmartApp.instance.applicationContext).asBitmap().load(url)
                    .submit().get()
            } catch (e: Exception) {
                // 可以根据具体的异常类型进行不同的处理
                e.printStackTrace()
                null
            }
        }
    }

    fun sendNotify(list: List<PushModel>) {
        if (isLockScreen()) {
            msgList = list
            if (BuildConfig.DEBUG) Log.d("TAG_FCM_MSG", "已锁屏，等待亮屏发送")
        } else {
            if (BuildConfig.DEBUG) Log.d("TAG_FCM_MSG", "未锁屏，发送")
            AppEvent.event("maigic_f_send_N")
            showNotify(list)
        }
    }

    private fun isLockScreen(): Boolean {
        val isLockSc = try {
            (SmartApp.instance.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).isKeyguardLocked
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return isLockSc
    }


    fun showNotify(list: List<PushModel>) {
        SmartApp.instance.scope.launch {
            list.forEach {
                val manager = NotificationManagerCompat.from(SmartApp.instance)
                val bitmap = requestBitMap(it.coverUrl)
                withContext(Dispatchers.Main) {
                    manager.cancel(it.id)
                    showPushMsg(
                        manager,
                        it.id,
                        bitmap, it.content, it.action, it.url, "push_message"
                    )
                }
                delay(1500)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showPushMsg(
        manager: NotificationManagerCompat,
        id: Int,
        bitmap: Bitmap?,
        content: String,
        action: String,
        pUrl: String,
        channelId: String
    ) {
        val remoteView =
            RemoteViews(SmartApp.instance.packageName, R.layout.layout_push_small)
        val remoteViewBig =
            RemoteViews(SmartApp.instance.packageName, R.layout.layout_push_big)
        val builder = NotificationCompat.Builder(SmartApp.instance, channelId)
        builder.setOngoing(true)
        builder.setGroupSummary(false)
        builder.setGroup(channelId)
        builder.setAutoCancel(true)
        builder.setSmallIcon(R.drawable.scan)
        builder.priority = NotificationManager.IMPORTANCE_HIGH

        remoteView.setTextViewText(R.id.btn_action, action)
        remoteView.setTextViewText(R.id.tv_title, content)
        if (bitmap == null) {
            remoteView.setImageViewResource(R.id.iv_img, R.drawable.shape_image)
            remoteViewBig.setImageViewResource(R.id.iv_img, R.drawable.shape_image)
        } else {
            remoteView.setImageViewBitmap(R.id.iv_img, bitmap)
            remoteViewBig.setImageViewBitmap(R.id.iv_img, bitmap)
        }

        remoteViewBig.setTextViewText(R.id.btn_action, action)
        remoteViewBig.setTextViewText(R.id.tv_title, content)

        val peIntent = intentAction(SmartApp.instance, pUrl, id)
        builder.setContentIntent(peIntent)
        builder.setContent(remoteView)

        builder.setCustomBigContentView(remoteViewBig)

        builder.setStyle(NotificationCompat.BigPictureStyle())
        manager.notify(id, builder.build())
    }


    private fun intentAction(context: Context, url: String, id: Int): PendingIntent {
        val intent = Intent(context, StartActivity::class.java)
        intent.action = "VIDEO_PUSH"
        intent.putExtra("url", url)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.setPackage(context.packageName)
        return PendingIntent.getActivity(
            context, id, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

}
