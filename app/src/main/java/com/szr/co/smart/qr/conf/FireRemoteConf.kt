package com.szr.co.smart.qr.conf

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.szr.co.smart.qr.manager.UserManager
import com.szr.co.smart.qr.model.AppSwitchType
import com.szr.co.smart.qr.utils.Utils
import kotlin.apply
import kotlin.run

class FireRemoteConf {

    interface Callback {
        fun onConfigUpdate()
    }


    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { FireRemoteConf() }
    }

    private var mCallback: Callback? = null

    fun initConfig(callback: Callback) {
        mCallback = callback
        Firebase.remoteConfig.run {
            setDefaultsAsync(hashMapOf<String?, Any?>().apply {
                put("sq_start_type", AppSwitchType.buser.ordinal)
                put(
                    "sq_ad_conf",
                    "ewogICAgInNxX25hdGkiOiB7CiAgICAgICAgInNxX2t5X2xpc3QiOiBbCiAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICJzcV9reSI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMjI0NzY5NjExMCIsCiAgICAgICAgICAgICAgICAic3FfcHIiOiAxCiAgICAgICAgICAgIH0KICAgICAgICBdLAogICAgICAgICJzcV9teF9jIjogMgogICAgfSwKICAgICJzcV9wbGEiOiB7CiAgICAgICAgInNxX2t5X2xpc3QiOiBbCiAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICJzcV9reSI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMTAzMzE3MzcxMiIsCiAgICAgICAgICAgICAgICAic3FfcHIiOiAxCiAgICAgICAgICAgIH0KICAgICAgICBdLAogICAgICAgICJzcV9teF9jIjogMgogICAgfSwKICAgICJzcV9zcGwiOiB7CiAgICAgICAgInNxX2t5X2xpc3QiOiBbCiAgICAgICAgICAgIHsKICAgICAgICAgICAgICAJInN1Yl90eXBlIjoicGxhIiwKICAgICAgICAgICAgICAgICJzcV9reSI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMTAzMzE3MzcxMiIsCiAgICAgICAgICAgICAgICAic3FfcHIiOiAxCiAgICAgICAgICAgIH0sCiAgICAgICAgICB7CiAgICAgICAgICAgIAkic3ViX3R5cGUiOiJzcGwiLAogICAgICAgICAgICAgICAgInNxX2t5IjogImNhLWFwcC1wdWItMzk0MDI1NjA5OTk0MjU0NC85MjU3Mzk1OTIxIiwKICAgICAgICAgICAgICAgICJzcV9wciI6IDEKICAgICAgICAgICAgfQogICAgICAgIF0sCiAgICAgICAgInNxX214X2MiOiAxCiAgICB9Cn0="
                )
                put(
                    "sq_adp_conf",
                    "eyJzcV9zdGFydCI6Miwic3FfbG5nX24iOjIsInNxX2xuZ19pIjoxLCJzcV9iYWNrX2kiOjEsInNxX21fY2xrX2kiOjEsInNxX21fbiI6Miwic3FfaGlzdG9yeV9uIjoyLCJzcV9xcmNfbiI6MSwic3FfcXJjX2Nsa19pIjoyLCJzcV9xcl9yZXN1bHRfbiI6Miwic3FfcXJfY2xrX3NhdmVfaSI6MSwic3FfcXJfc2Nhbl9pIjoxLCJzcV9xcl9zY2FuX24iOjEsInNxX290aGVyX24iOjF9"
                )
                put(
                    "sq_default_url",
                    "aHR0cHM6Ly93d3cuaW5zdGFncmFtLmNvbS9yZWVsL0RKNnd1MW5TRTRFLw=="
                )
                put("sq_fb_ra", 1.0)
                put("sq_rev_paid", -1.0)
                put("sq_user_ctime", 48)
                put("sq_gu_url", "aHR0cHM6Ly93d3cuaW5zdGFncmFtLmNvbS9yZWVscy9ETUlwTWpWU1RCQi8=")
                put("sq_cloc",false)
                put("sq_fb_ad_threshold",0.0)

            })
            addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    mCallback?.onConfigUpdate()
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                }
            })
        }

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                mCallback?.onConfigUpdate()
            }
        }
    }


    val hotStartConf: Int
        get() {
            return Firebase.remoteConfig.getLong("sq_start_type").toInt()
        }

    val fbMul: Double
        get() {
            return Firebase.remoteConfig.getDouble("sq_fb_ra")
        }

    val fbConf: String
        get() {
            return Firebase.remoteConfig.getString("sq_fb_conf")
        }


    val adPaidUploadValue: Double
        get() {
            return Firebase.remoteConfig.getDouble("sq_rev_paid")
        }

    val adConfig: String
        get() {
            return Firebase.remoteConfig.getString("sq_ad_conf")
        }
    val adpConfig: String
        get() {
            return Firebase.remoteConfig.getString("sq_adp_conf")
        }

    val changeUserTypeTime: Int
        get() {
            return Firebase.remoteConfig.getLong("sq_user_ctime").toInt()
        }

    val guideDefaultUrl: String
        get() {
            val url = Firebase.remoteConfig.getString("sq_gu_url")
            return Utils.decodeBase64(url)
        }

    val refConfig: String
        get() {
            return com.google.firebase.Firebase.remoteConfig.getString("sq_ref_config")
        }

    val clocEnable: Boolean
        get() {
            return com.google.firebase.Firebase.remoteConfig.getBoolean("sq_cloc")
        }

    val fbAdThreshold: Double
        get() {
            return com.google.firebase.Firebase.remoteConfig.getDouble("sq_fb_ad_threshold")
        }
}