package com.szr.co.smart.qr.manager

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.szr.co.smart.qr.BuildConfig
import com.szr.co.smart.qr.conf.FireRemoteConf
import com.szr.co.smart.qr.data.DataSetting
import com.szr.co.smart.qr.logic.PushTokenLogic

class UserManager {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { UserManager() }
    }

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