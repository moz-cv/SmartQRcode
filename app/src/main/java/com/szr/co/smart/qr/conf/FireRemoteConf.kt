package com.szr.co.smart.qr.conf

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
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
                    "eyJzcV9uYXRpIjp7InNxX2t5X2xpc3QiOlt7InNxX2t5IjoiY2EtYXBwLXB1Yi01Mjc5MzM0NDA1NTUxMjYyLzc3NTI1MzI3MDMiLCJzcV9wciI6M30seyJzcV9reSI6ImNhLWFwcC1wdWItNTI3OTMzNDQwNTU1MTI2Mi8zNTkwOTc2MTYyIiwic3FfcHIiOjJ9LHsic3Ffa3kiOiJjYS1hcHAtcHViLTUyNzkzMzQ0MDU1NTEyNjIvNTc4NDg0NDkxNiIsInNxX3ByIjoxfV0sInNxX214X2MiOjJ9LCJzcV9wbGEiOnsic3Ffa3lfbGlzdCI6W3sic3Ffa3kiOiJjYS1hcHAtcHViLTUyNzkzMzQ0MDU1NTEyNjIvNjg3NTYxNTA1NyIsInNxX3ByIjozfSx7InNxX2t5IjoiY2EtYXBwLXB1Yi01Mjc5MzM0NDA1NTUxMjYyLzk3MjQwODk5MjQiLCJzcV9wciI6Mn0seyJzcV9reSI6ImNhLWFwcC1wdWItNTI3OTMzNDQwNTU1MTI2Mi85MDY1NjE0MzcxIiwic3FfcHIiOjF9XSwic3FfbXhfYyI6Mn0sInNxX3NwbCI6eyJzcV9reV9saXN0IjpbeyJzdWJfdHlwZSI6InNwbCIsInNxX2t5IjoiY2EtYXBwLXB1Yi01Mjc5MzM0NDA1NTUxMjYyLzgwNDgwNDYxNzciLCJzcV9wciI6NH0seyJzdWJfdHlwZSI6InBsYSIsInNxX2t5IjoiY2EtYXBwLXB1Yi01Mjc5MzM0NDA1NTUxMjYyLzg3OTQxNTE2MzciLCJzcV9wciI6M30seyJzdWJfdHlwZSI6InBsYSIsInNxX2t5IjoiY2EtYXBwLXB1Yi01Mjc5MzM0NDA1NTUxMjYyLzY3MzQ5NjQ1MDYiLCJzcV9wciI6Mn0seyJzdWJfdHlwZSI6InBsYSIsInNxX2t5IjoiY2EtYXBwLXB1Yi01Mjc5MzM0NDA1NTUxMjYyLzk0NDIwNjg2NDYiLCJzcV9wciI6MX1dLCJzcV9teF9jIjoxfX0="
                )
                put(
                    "sq_adp_conf",
                    "eyJzcV9zdGFydCI6Miwic3FfbG5nX24iOjIsInNxX2xuZ19pIjoxLCJzcV9iYWNrX2kiOjEsInNxX21fY2xrX2kiOjEsInNxX21fbiI6Miwic3FfaGlzdG9yeV9uIjoyLCJzcV9xcmNfbiI6MSwic3FfcXJjX2Nsa19pIjoyLCJzcV9xcl9yZXN1bHRfbiI6Miwic3FfcXJfY2xrX3NhdmVfaSI6MSwic3FfcXJfc2Nhbl9pIjoxLCJzcV9xcl9zY2FuX24iOjF9"
                )
                put(
                    "sq_default_url",
                    "aHR0cHM6Ly93d3cuaW5zdGFncmFtLmNvbS9yZWVsL0RKNnd1MW5TRTRFLw=="
                )
                put("sq_fb_ra", 1.0)
                put("sq_rev_paid", -1.0)
                put("sq_user_ctime", 48)
                put("sq_gu_url", "aHR0cHM6Ly93d3cuaW5zdGFncmFtLmNvbS9yZWVscy9ETUlwTWpWU1RCQi8=")
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
}