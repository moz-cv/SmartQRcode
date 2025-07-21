package com.szr.co.smart.qr.manager

import android.app.Application
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.reyun.solar.engine.OnAttributionListener
import com.reyun.solar.engine.SolarEngineConfig
import com.reyun.solar.engine.SolarEngineManager
import com.szr.co.smart.qr.BuildConfig
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.conf.FireRemoteConf
import com.szr.co.smart.qr.constant.Constants
import com.szr.co.smart.qr.data.DataSetting
import com.szr.co.smart.qr.event.AppAdTrack
import com.szr.co.smart.qr.event.AppEvent
import com.szr.co.smart.qr.logic.PushTokenLogic
import com.szr.co.smart.qr.utils.Utils
import org.json.JSONObject

class UserManager {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { UserManager() }
    }

    val mThirdUserCheck = ThirdUserCheck()

    fun buyUser(): Boolean {
        if (BuildConfig.DEBUG) {
            return true
        }
        return DataSetting.instance.vipUser || DataSetting.instance.autoVip
    }


    fun checkPushUerStatus(time: Long) {
        if (buyUser()) return
        val lastTime = DataSetting.instance.fcmTime
        if (lastTime == 0L) {
            DataSetting.instance.fcmTime = time
            return
        }
        val change = (time - lastTime) > 60 * 60 * FireRemoteConf.instance.changeUserTypeTime
        if (change) {
            DataSetting.instance.autoVip = true
            updateFireUType()
            PushTokenLogic.uploadToken()
        }
    }

    @Synchronized
    fun updateUser(vip: Boolean) {
        val localBuy = DataSetting.instance.vipUser
        if (localBuy) return
        DataSetting.instance.vipUser = vip
        updateFireUType()
        PushTokenLogic.uploadToken()
    }

    private fun updateFireUType() {
        Firebase.analytics.setUserProperty(
            "sq_user_status", if (buyUser()) "sq" else "zrk"
        )
    }


}

class ThirdUserCheck {

    fun initFbAndSe() {
        val fbInfo = getFbInfo()
        if (fbInfo == null) return
        initFacebook(fbInfo.first, fbInfo.second)
        initSolarEngine(SmartApp.instance, fbInfo.first)
    }

    private fun getFbInfo(): Pair<String, String>? {
        val fbInfo = FireRemoteConf.instance.fbConf
        if (fbInfo.isEmpty()) return null
        val info = Utils.decodeBase64(fbInfo)
        if (info.isEmpty()) return null
        val json = JSONObject(info)
        val fbId = json.getString("sq_fb_id")
        val fbToken = json.getString("sq_fb_token")
        return Pair(fbId, fbToken)
    }

    private fun initFacebook(fbId: String, fbToken: String) {
        try {
            FacebookSdk.setApplicationId(fbId)
            FacebookSdk.setClientToken(fbToken)
            FacebookSdk.fullyInitialize()
            FacebookSdk.sdkInitialize(SmartApp.instance)
            AppEventsLogger.activateApp(SmartApp.instance)

            AppAdTrack.fbPaidRetry()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun initInstallReferrer(application: Application) {
        if (DataSetting.instance.installReferrerFinish) return

        AppEvent.event("sq_ref_start")
        InstallReferrerClient.newBuilder(application).build().run {
            startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(p0: Int) {
                    if (p0 == InstallReferrerClient.InstallReferrerResponse.OK) {
                        val referrer = try {
                            installReferrer?.installReferrer
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                        if (!referrer.isNullOrEmpty()) {

                            DataSetting.instance.referValue = referrer

                            DataSetting.instance.installReferrerFinish = true
                            val isVip = checkAccountVipByInstallReferrer(referrer)
                            if (isVip) {
                                AppEvent.event("sq_ref_buser")
                            } else {
                                AppEvent.event("sq_ref_nuser")
                            }
                            UserManager.instance.updateUser(isVip)
                        }
                    }
                    try {
                        endConnection()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {

                }
            })
        }

    }

    private fun initSolarEngine(application: Application, fbId: String) {
        AppEvent.event("sq_se_start")
        SolarEngineManager.getInstance().preInit(application, Constants.SE_APP_ID)
        val builder = SolarEngineConfig.Builder()
        builder.setFbAppID(fbId)
        val config = builder.build()
        config.setOnAttributionListener(object : OnAttributionListener {
            override fun onAttributionSuccess(attribution: JSONObject) {
                //获取归因结果成功时执行的动作
                val channelId = attribution.get("channel_id")
                val channelUser = !(channelId == "-1" || channelId == -1)
                if (channelUser) {
                    AppEvent.event("sq_se_buser")
                } else {
                    AppEvent.event("sq_se_nuser")
                }
//                Log.d("TAG_ACCOUNT_SSS", "SolarEngine:${channelUser}--:$channelId")
                UserManager.instance.updateUser(channelUser)
            }

            override fun onAttributionFail(errorCode: Int) {
                //获取归因结果失败时执行的动作
            }
        })
        SolarEngineManager.getInstance().initialize(
            application, Constants.SE_APP_ID, config
        ) { code ->
            if (code == 0) {
                //初始化成功
                AppAdTrack.sePaidRetry()
            } else {
                //初始化失败，具体失败原因参考下方code码释义
            }
        }
    }

    private fun checkAccountVipByInstallReferrer(str: String?): Boolean {
        if (str.isNullOrEmpty()) return false
        return str.contains("ig4a", true) || str.contains(
            "instagram", true
        ) || str.contains("fb4a", true) || str.contains(
            "facebook", true
        ) || str.contains(
            "fb", true
        )
    }
}
