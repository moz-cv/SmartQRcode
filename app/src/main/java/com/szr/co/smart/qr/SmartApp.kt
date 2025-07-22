package com.szr.co.smart.qr

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.szr.co.smart.qr.activity.ScanActivity
import com.szr.co.smart.qr.activity.StartActivity
import com.szr.co.smart.qr.bill.position.ViPositionHelper
import com.szr.co.smart.qr.bill.type.ViBillType
import com.szr.co.smart.qr.conf.FireRemoteConf
import com.szr.co.smart.qr.data.DataSetting
import com.szr.co.smart.qr.event.AppEvent
import com.szr.co.smart.qr.logic.PushTokenLogic
import com.szr.co.smart.qr.manager.UserManager
import com.szr.co.smart.qr.model.checkAppSwitchType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class SmartApp : Application(), FireRemoteConf.Callback {

    companion object {
        lateinit var instance: SmartApp
    }

    val scope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Firebase.crashlytics.recordException(throwable)
        })
    }

    private val activityList = mutableListOf<Activity>()
    private var pageCount = 0

    var obtainUpm = false
    override fun onCreate() {
        super.onCreate()
        instance = this
        DataSetting.instance.initialize(this)
        MobileAds.initialize(this)
        PushTokenLogic.uploadToken()
        FireRemoteConf.instance.initConfig(this)
        registerActivityLifecycleCallbacks(mAppActivityCycle)
        UserManager.instance.mThirdUserCheck.initInstallReferrer(this)
        UserManager.instance.mThirdUserCheck.initFbAndSe()

        AppEvent.event("application_start")
    }

    override fun onConfigUpdate() {
        UserManager.instance.mThirdUserCheck.initFbAndSe()
        ViBillType.clean()
        ViPositionHelper.clean()
    }


    private val mAppActivityCycle = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(
            activity: Activity,
            savedInstanceState: Bundle?
        ) {
            activityList.add(activity)
        }


        override fun onActivityStarted(activity: Activity) {
            if (pageCount == 0) {
                if (isStartHot(activity)) {
                    val intent = Intent(this@SmartApp, StartActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("from_application", true)
                    startActivity(intent)
                }
            }
            pageCount++
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
            pageCount--
            if (pageCount == 0) {
                //TODO
            }
        }

        override fun onActivitySaveInstanceState(
            activity: Activity,
            outState: Bundle
        ) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            activityList.remove(activity)
        }
    }

    private fun isStartHot(activity: Activity): Boolean {
        val activityMeet =
            activity !is StartActivity && activity !is ScanActivity && !hasAdActivity()
        if (!activityMeet) return false
        val state = FireRemoteConf.instance.hotStartConf
        return checkAppSwitchType(state)
    }

    private fun hasAdActivity(): Boolean {
        for (activity in activityList) {
            if (activity is AdActivity) {
                return true
            }
        }
        return false
    }

    private fun initFlipper() {
//        SoLoader.init(this, false)
//        val client = AndroidFlipperClient.getInstance(this)
//        client.addPlugin(DatabasesFlipperPlugin(this))
//        client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
//        client.start()
    }
}