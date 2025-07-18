package com.szr.co.smart.qr

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.szr.co.smart.qr.activity.StartActivity
import com.szr.co.smart.qr.conf.FireRemoteConf
import com.szr.co.smart.qr.data.DataSetting
import com.szr.co.smart.qr.logic.PushTokenLogic
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class SmartApp : Application(),FireRemoteConf.Callback {

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

    override fun onCreate() {
        super.onCreate()
        instance = this
        DataSetting.instance.initialize(this)
        PushTokenLogic.uploadToken()
        FireRemoteConf.instance.initConfig(this)
        registerActivityLifecycleCallbacks(mAppActivityCycle)
    }

    override fun onConfigUpdate() {
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
                if (canHotStart()) {
                    val intent = Intent(this@SmartApp, StartActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("from_app", true)
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
}