package com.szr.co.smart.qr.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.szr.co.smart.qr.BuildConfig
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.bill.callbacks.ViOnBillCallback
import com.szr.co.smart.qr.bill.info.ViBaseBill
import com.szr.co.smart.qr.bill.position.ViBillPosition
import com.szr.co.smart.qr.bill.position.ViPositionHelper
import com.szr.co.smart.qr.data.DataSetting
import com.szr.co.smart.qr.databinding.ActivityStartBinding
import com.szr.co.smart.qr.event.AppAdTrack
import com.szr.co.smart.qr.event.AppEvent
import com.szr.co.smart.qr.logic.IntentLogic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartActivity : BaseActivity<ActivityStartBinding>() {

    private var hostStart = false

    override fun inflateBinding(): ActivityStartBinding {
        return ActivityStartBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        IntentLogic.instance.parseIntentData(intent)
        hostStart = intent.getBooleanExtra("from_application", false)
        val startValue = if (hostStart) "hs" else "ls"
        AppEvent.eventValue("app_sq_show", startValue)
        checkNotifyPermission {
            checkGoogleUmp {
                loadAdDataA()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeOutJob?.cancel()
        timeOutJob = null
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        IntentLogic.instance.parseIntentData(intent)
    }

    private fun checkNotifyPermission(complete: () -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            complete.invoke()
            return
        }
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            complete.invoke()
        }.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun checkGoogleUmp(complete: () -> Unit) {
        if (SmartApp.instance.obtainUpm) {
            complete.invoke()
            return
        }

        val builder = ConsentRequestParameters.Builder()
        if (BuildConfig.DEBUG) {
            val debugSettings = ConsentDebugSettings.Builder(this)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA) //添加测试地址(欧洲)
                .addTestDeviceHashedId("A784254FA42B09231DDFAF10BA785129") //添加测试设备
                .build()
            builder.setConsentDebugSettings(debugSettings) //正式环境需要删
        }
        val params = builder.build()
        val consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(this, params, {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { _ ->
                complete.invoke()
                if (consentInformation.canRequestAds()) {
                    try {
                        MobileAds.initialize(SmartApp.instance)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        }, {
            complete.invoke()
        })
    }

    private var isTimeOut = false
    private var timeOutJob: Job? = null
    private var openLoadComplete = -1

    private fun loadAdDataA() {

        AppAdTrack.logAdEnter(ViBillPosition.POS_OPEN.position)
        timeOutJob = SmartApp.instance.scope.launch {
            delay(15000)
            isTimeOut = true
            withContext(Dispatchers.Main) {
                showAd(ViPositionHelper.getBill(ViBillPosition.POS_OPEN), true)
            }
        }
        ViBillPosition.POS_OPEN.type.loadAd(ViBillPosition.POS_OPEN) {
            if (isTimeOut) return@loadAd
            if (it != null) {
                openLoadComplete = 1
                showAd(it)
            } else {
                val ad = ViPositionHelper.getBill(ViBillPosition.POS_OPEN)
                openLoadComplete = if (ad == null) 0 else 1
                showAd(ad)
            }
        }
        preLoadAd()
    }

    private fun showAd(ad: ViBaseBill?, isSkipMain: Boolean = false) {
        if (ad != null) {
            timeOutJob?.cancel()
            ad.setOnBillCallback(object : ViOnBillCallback {
                override fun onDismiss() {
                    super.onDismiss()
                    toApp()
                }
            }).show(this, ViBillPosition.POS_OPEN.position)
        } else {
            if (isSkipMain) {
                toApp()
            }
        }
    }

    private fun toApp() {
        if (!DataSetting.instance.langGuide) {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        } else {
            if (!hostStart) startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

    private fun preLoadAd() {
        if (DataSetting.instance.langGuide) {
            ViBillPosition.POS_LAN_INTERS.run {
                type.preloadAd(this)
            }
            ViBillPosition.POS_LAN_NATIVE.run {
                type.preloadAd(this)
            }

        } else {
            ViBillPosition.POS_MAIN_NATIVE.run {
                type.preloadAd(this)
            }
            ViBillPosition.POS_MAIN_CLICK_INTERS.run {
                type.preloadAd(this)
            }
        }
    }
}