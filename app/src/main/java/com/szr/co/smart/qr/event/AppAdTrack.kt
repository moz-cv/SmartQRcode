package com.szr.co.smart.qr.event

import android.os.Bundle
import android.util.Log
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsConstants.EVENT_NAME_AD_CLICK
import com.facebook.appevents.AppEventsConstants.EVENT_NAME_AD_IMPRESSION
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.reyun.solar.engine.SolarEngineManager
import com.reyun.solar.engine.infos.SEAdImpEventModel
import com.szr.co.smart.qr.BuildConfig
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.conf.FireRemoteConf
import com.szr.co.smart.qr.data.DataSetting
import java.math.BigDecimal
import java.util.Currency


data class FbAdPaidModel(
    @SerializedName("er") var click: FbAdPaidDetailModel?,
    @SerializedName("wr") var show: FbAdPaidDetailModel?
)


data class SEAdPaidModel(
    @SerializedName("eqwe") var sourceName: String,
    @SerializedName("dasda") var adType: Int,
    @SerializedName("czxc") var aId: String,
    @SerializedName("gfdg") var valueMicros: Long,
    @SerializedName("ytr") var currencyCode: String
)

data class FbAdPaidDetailModel(
    @SerializedName("ht") var valueMicros: Long, @SerializedName("erwer") var currencyCode: String
)


object AppAdTrack {

    fun logAdEnter(pos: String) {
        if (BuildConfig.DEBUG) Log.d("TAG_EV_AD", "广告场景到达：${pos}")
        Firebase.analytics.logEvent("sq_ad_pos_enter", Bundle().apply {
            putString("sq", pos)
        })
    }

    fun logAdShow(pos: String) {
        if (BuildConfig.DEBUG) Log.d("TAG_EV_AD", "广告展示：${pos}")
        Firebase.analytics.logEvent("sq_ad_pos_show", Bundle().apply {
            putString("sq", pos)
        })
    }

    fun logAdShowSuc(pos: String) {
        if (BuildConfig.DEBUG) Log.d("TAG_EV_AD", "广告展示成功：${pos}")
        Firebase.analytics.logEvent("sq_ad_pos_show_suc", Bundle().apply {
            putString("sq", pos)
        })
    }

    fun logAdClick(pos: String) {
        if (BuildConfig.DEBUG) Log.d("TAG_EV_AD", "广告点击：${pos}")
        Firebase.analytics.logEvent("sq_ad_pos_show_cli", Bundle().apply {
            putString("sq", pos)
        })
    }

    private val gson by lazy { Gson() }
    private var adPaidSeList: MutableList<SEAdPaidModel> = mutableListOf()
    private var adPaidFb: FbAdPaidModel? = null

    init {
        initAdPaid()
    }

    private fun initAdPaid() {
        try {
            adPaidFb = gson.fromJson(
                DataSetting.instance.fbAdPaid, FbAdPaidModel::class.java
            )
        } catch (_: Exception) {
        }

        try {
            val cacheList = gson.fromJson<List<SEAdPaidModel>>(
                DataSetting.instance.seAdPaid, object : TypeToken<List<SEAdPaidModel>>() {}.type
            )
            adPaidSeList.addAll(cacheList)
        } catch (_: Exception) {
        }
    }


    private fun fbPaid(valueMicros: Long, currencyCode: String) {
        val value = valueMicros / 1000000.0
        if (value < FireRemoteConf.instance.fbAdThreshold) {
            return
        }

        if (!checkFbInit(false, valueMicros, currencyCode)) return

        val newValueMicros = if (adPaidFb != null) {
            val lastValue = adPaidFb!!.show?.valueMicros ?: 0
            adPaidFb?.show = null
            try {
                DataSetting.instance.fbAdPaid = gson.toJson(adPaidFb)
            } catch (_: Exception) {
            }
            lastValue + valueMicros
        } else {
            valueMicros
        }

        AppEventsLogger.newLogger(SmartApp.instance.applicationContext).logPurchase(
            BigDecimal((newValueMicros / 1000000.0) * FireRemoteConf.instance.fbMul),
            Currency.getInstance(currencyCode)
        )

        //Facebook 购买事件
        val parameters = Bundle()
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currencyCode)
        AppEventsLogger.newLogger(SmartApp.instance.applicationContext).logEvent(
            EVENT_NAME_AD_IMPRESSION,
            (newValueMicros / 1000000.0) * FireRemoteConf.instance.fbMul,
            parameters
        )
    }

    /**
     * fb：广告点击
     */
    fun fbAdClick(valueMicros: Long, currencyCode: String) {
        val value = valueMicros / 1000000.0
        if (value < FireRemoteConf.instance.fbAdThreshold) {
            return
        }

        if (!checkFbInit(true, valueMicros, currencyCode)) return

        val newValueMicros = if (adPaidFb != null) {
            val lastValue = adPaidFb!!.click?.valueMicros ?: 0
            adPaidFb?.click = null
            try {
                DataSetting.instance.fbAdPaid = gson.toJson(adPaidFb)
            } catch (_: Exception) {
            }
            lastValue + valueMicros
        } else {
            valueMicros
        }

        //Facebook 购买事件
        val parameters = Bundle()
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currencyCode)
        AppEventsLogger.newLogger(SmartApp.instance.applicationContext).logEvent(
            EVENT_NAME_AD_CLICK,
            (newValueMicros / 1000000.0) * FireRemoteConf.instance.fbMul,
            parameters
        )
    }

    private fun seAdPaid(
        sourceName: String?, type: Int, id: String, valueMicros: Long, currencyCode: String
    ) {
        if (!seCheckFbInit(sourceName ?: "", type, id, valueMicros, currencyCode)) return
        val seAdImpEventModel = SEAdImpEventModel().apply {
            //变现平台名称
            setAdNetworkPlatform(sourceName ?: "")
            //聚合平台标识,admob SDK 设置成 "admob"
            setMediationPlatform("admob")
            //展示广告的类型，实际接入的广告类型,以此例激励视频为例adType = 1
            setAdType(type)
            //变现平台的应用ID
//            setAdNetworkAppID(adSourceId)
            //变现平台的变现广告位ID
            setAdNetworkADID(id)
            //广告ECPM
            setEcpm(valueMicros / 1000.0)
            //变现平台货币类型
            setCurrencyType(currencyCode)
            //填充成功填TRUE即可
            setRenderSuccess(true)
        }
        SolarEngineManager.getInstance().trackAdImpression(seAdImpEventModel)
    }

    private fun checkFbInit(click: Boolean, valueMicros: Long, currencyCode: String): Boolean {
        if (FacebookSdk.isInitialized()) {
            return true
        }
        if (adPaidFb == null) {
            adPaidFb = FbAdPaidModel(null, null)
        }

        if (click) {
            val show = adPaidFb?.show
            if (show == null) {
                adPaidFb?.show = FbAdPaidDetailModel(valueMicros, currencyCode)
            } else {
                adPaidFb?.show?.valueMicros += valueMicros
                adPaidFb?.show?.currencyCode = currencyCode
            }
        } else {
            val click = adPaidFb?.click
            if (click == null) {
                adPaidFb?.click = FbAdPaidDetailModel(valueMicros, currencyCode)
            } else {
                adPaidFb?.click?.valueMicros += valueMicros
                adPaidFb?.click?.currencyCode = currencyCode
            }
        }
        try {
            DataSetting.instance.fbAdPaid = gson.toJson(adPaidFb)
        } catch (_: Exception) {
        }
        return false
    }

    fun seCheckFbInit(
        sourceName: String, adType: Int, adId: String, valueMicros: Long, currencyCode: String
    ): Boolean {
        if (FacebookSdk.isInitialized()) {
            return true
        }
        val adPaidSe = SEAdPaidModel(sourceName, adType, adId, valueMicros, currencyCode)
        adPaidSeList.add(adPaidSe)
        try {
            DataSetting.instance.seAdPaid = gson.toJson(adPaidSeList)
        } catch (_: Exception) {
        }
        return false
    }

    ///---------------
    private fun adPaidReturn(paid: Long) {
        if (DataSetting.instance.adPaidReturnUpload) return
        if (!FacebookSdk.isInitialized()) return
        val confValue = FireRemoteConf.instance.adPaidUploadValue
        if (confValue == -1.0) return

        val value = DataSetting.instance.adPaidReturn
        val newValue = value + paid
        DataSetting.instance.adPaidReturn = newValue
        if (newValue >= confValue) {
            val parameters = Bundle()
            parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD")
            AppEventsLogger.newLogger(SmartApp.instance.applicationContext).logEvent(
                "sq_fadValue", (newValue / 1000000.0), parameters
            )
            DataSetting.instance.adPaidReturnUpload = true
        }
    }

    fun adPaid(
        sourceName: String?, type: Int, id: String, valueMicros: Long, currencyCode: String
    ) {
        seAdPaid(sourceName, type, id, valueMicros, currencyCode)
        fbPaid(valueMicros, currencyCode)
        adPaidReturn(valueMicros)
    }


    fun fbPaidRetry() {
        if (adPaidFb == null) {
            return
        }

        // 深拷贝
        val clonedInfo = adPaidFb!!.copy()
        adPaidFb = null
        DataSetting.instance.fbAdPaid = ""

        val showFb = clonedInfo.show
        if (showFb != null) {
            fbPaid(showFb.valueMicros, showFb.currencyCode)
        }

        val clickFb = clonedInfo.click
        if (clickFb != null) {
            fbAdClick(clickFb.valueMicros, clickFb.currencyCode)
        }
    }

    fun sePaidRetry() {
        //se
        if (adPaidSeList.isEmpty()) {
            return
        }
        val clonedList = adPaidSeList.map { it.copy() }.toMutableList()
        adPaidSeList.clear()
        DataSetting.instance.seAdPaid = ""
        clonedList.forEach {
            seAdPaid(
                it.sourceName, it.adType, it.aId, it.valueMicros, it.currencyCode
            )
        }
    }

}