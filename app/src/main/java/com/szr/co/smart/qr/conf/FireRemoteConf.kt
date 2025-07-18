package com.szr.co.smart.qr.conf

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.szr.co.smart.qr.manager.UserManager
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
                //TODO
                put("sq_start_type", 1)
                put(
                    "sq_ad_conf",
                    "ewogICAgInNxX25hdGkiOiB7CiAgICAgICAgInNxX2t5X2xpc3QiOiBbCiAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICJzcV9reSI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMjI0NzY5NjExMCIsCiAgICAgICAgICAgICAgICAic3FfcHIiOiAxCiAgICAgICAgICAgIH0KICAgICAgICBdLAogICAgICAgICJzcV9teF9jIjogMgogICAgfSwKICAgICJzcV9wbGEiOiB7CiAgICAgICAgInNxX2t5X2xpc3QiOiBbCiAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICJzcV9reSI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMTAzMzE3MzcxMiIsCiAgICAgICAgICAgICAgICAic3FfcHIiOiAxCiAgICAgICAgICAgIH0KICAgICAgICBdLAogICAgICAgICJzcV9teF9jIjogMgogICAgfSwKICAgICJzcV9zcGwiOiB7CiAgICAgICAgInNxX2t5X2xpc3QiOiBbCiAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICJzcV9reSI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvOTI1NzM5NTkyMSIsCiAgICAgICAgICAgICAgICAic3FfcHIiOiAxCiAgICAgICAgICAgIH0KICAgICAgICBdLAogICAgICAgICJzcV9teF9jIjogMQogICAgfQp9"
                )
                put(
                    "sq_adp_conf",
                    "ewogICAgIm1hZ2ljX3N0YXJ0IjogMiwKICAgICJtYWdpY19sYW5fbmF0aSI6IDEsCiAgICAibWFnaWNfbGFuX2ludGVycyI6IDEsCiAgICAibWFnaWNfcGFyX2NvbV9pbnRlcnMiOiAyLAogICAgIm1hZ2ljX3Bhcl9uYXRpIjogMSwKICAgICJtYWdpY19wYXJfY2xpX2Rvd25faW50ZXJzIjogMSwKICAgICJtYWdpY19iYWNrX2ludGVycyI6IDEsCiAgICAibWFnaWNfaG1fY2xrX2ludGVycyI6IDEsCiAgICAibWFnaWNfZG93bl9zdWNfbmF0aSI6IDEsCiAgICAibWFnaWNfY2xrX3ZpZGVvc19pbnRlcnMiOiAxLAogICAgIm1hZ2ljX3NldF9uYXRpIjogMSwKICAgICJtYWdpY19leF9uYXRpIjogMSwKICAgICJtYWdpY19kb3duaW5nX25hdGkiOiAxLAogICAgIm1hZ2ljX2htX25hdGkiOiAyCn0="
                )
                put(
                    "sq_default_url",
                    "aHR0cHM6Ly93d3cuaW5zdGFncmFtLmNvbS9yZWVsL0RKNnd1MW5TRTRFLw=="
                )
                put("sq_fb_ra", 1.0)
                put("sq_rev_paid", -1.0)
                put("sq_user_ctime", 48)
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

    val defaultParseUrl: String
        get() {
            val url = Firebase.remoteConfig.getString("sqc_default_url")
            return Utils.decodeBase64(url)
        }
}