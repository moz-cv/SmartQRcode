package com.szr.co.smart.qr.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.util.Range
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.szr.co.smart.qr.BuildConfig
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.constant.Constants
import com.szr.co.smart.qr.data.DataSetting
import com.szr.co.smart.qr.event.AppEvent
import com.szr.co.smart.qr.logic.PushTokenLogic
import com.szr.co.smart.qr.manager.UserManager
import com.szr.co.smart.qr.model.PushModel
import com.szr.co.smart.qr.utils.Utils
import okio.ByteString.Companion.decodeBase64
import org.json.JSONArray
import java.util.Calendar
import kotlin.math.min

class FireMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        handleRemoteMessage(message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PushTokenLogic.uploadToken(token)
    }

    /**
     * 主流程：处理远程消息
     */
    private fun handleRemoteMessage(message: RemoteMessage) {
        AppEvent.event("magic_rec_f_msg")
        val data = message.data
        if (data.isEmpty()) {
            AppEvent.eventValue("magic_f_fail", "data is null1")
            return
        }
        val videoInfo = data["video_info"]
        if (videoInfo.isNullOrEmpty()) {
            AppEvent.eventValue("magic_f_fail", "data is null2")
            return
        }
        val pushTime = data["ts"]?.toLongOrNull() ?: 0L
        UserManager.instance.checkPushUerStatus(pushTime)
        if (!UserManager.instance.buyUser()) return

        // 时间范围校验
        data["date_range"]?.let {
            extractRange(it)?.let { range ->
                if (!isCurrentTimeInRange(range)) return
            }
        }

        if (BuildConfig.DEBUG) Log.d("TAG_FCM_MSG", "receiver:${Utils.decodeBase64(videoInfo)}")
        if (!hasNotifyPermission(SmartApp.instance.applicationContext)) return

        AppEvent.event("magic_f_hper")
        try {
            val jsonStr = Utils.decodeBase64(videoInfo)
            if (jsonStr.isEmpty()) return
            val listFcm = parseJsonArray(jsonStr)
            FireMessagePushLogic.instance.sendNotify(listFcm)
        } catch (e: Exception) {
            AppEvent.eventValue("magic_f_fail", "error-1110-${e.message}")
        }
    }

    /**
     * 提取时间范围
     */
    private fun extractRange(input: String): Range<Int>? {
        val numbers = Regex("\\d+").findAll(input).map { it.value.toInt() }.toList()
        return if (numbers.size == 2) Range(numbers[0], numbers[1]) else null
    }

    /**
     * 判断当前小时是否在范围内
     */
    private fun isCurrentTimeInRange(range: Range<Int>): Boolean {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return currentHour in range.lower until range.upper
    }

    /**
     * 判断是否有通知权限
     */
    private fun hasNotifyPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 解析推送内容
     */
    private fun parseJsonArray(jsonArrayString: String): List<PushModel> {
        val result = mutableListOf<PushModel>()
        val contentList = SmartApp.instance.resources.getStringArray(R.array.push_til)
        val actionList = SmartApp.instance.resources.getStringArray(R.array.push_content)
        val size = min(contentList.size, actionList.size)
        var startIndex = DataSetting.instance.pushIdIndex
        try {
            val jsonArray = JSONArray(jsonArrayString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val url = jsonObject.getString("or_url")
                val cover = jsonObject.getString("cover")
                val contentIndex = (0 until size).random()
                startIndex = (startIndex + 1) % Constants.pushListId.size
                val id = Constants.pushListId[startIndex]
                result.add(
                    PushModel(
                        id,
                        contentList[contentIndex],
                        actionList[contentIndex],
                        url,
                        cover
                    )
                )
            }
            DataSetting.instance.pushIdIndex = startIndex
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}