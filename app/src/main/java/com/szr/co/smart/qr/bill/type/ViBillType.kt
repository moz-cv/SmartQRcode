package com.szr.co.smart.qr.bill.type

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.Firebase
import com.szr.co.smart.qr.bill.cache.ViBillCache
import com.szr.co.smart.qr.bill.info.ViBannerInfo
import com.szr.co.smart.qr.bill.info.ViBaseBill
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.bill.info.ViBillInfo
import com.szr.co.smart.qr.bill.info.ViInterstitialInfo
import com.szr.co.smart.qr.bill.info.ViKeyInfo
import com.szr.co.smart.qr.bill.info.ViNativeInfo
import com.szr.co.smart.qr.bill.info.ViOpenInfo
import com.szr.co.smart.qr.bill.position.ViBillPosition
import com.szr.co.smart.qr.bill.position.ViPositionHelper
import com.szr.co.smart.qr.conf.FireRemoteConf
import com.szr.co.smart.qr.utils.Utils
import com.szr.co.smart.qr.utils.dpToPx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.math.pow

class ViBillType(val type: String) {

    companion object {
        val TYPE_BANNER by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ViBillType("grad_bar") }
        val TYPE_NATIVE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ViBillType("grad_nati") }
        val TYPE_INTERSTITIAL by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ViBillType("grad_pla") }
        val TYPE_OPEN by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ViBillType("grad_spl") }

        fun clean() {
            TYPE_BANNER.billInfo = null
            TYPE_NATIVE.billInfo = null
            TYPE_INTERSTITIAL.billInfo = null
            TYPE_OPEN.billInfo = null
        }
    }

    private var errorCount = 0
    private var errorJob: Job? = null
    private val cache = ViBillCache()
    private var loadingCount = 0
    private var completeCallback: ((ViBaseBill?) -> Unit)? = null

    fun getBill(): ViBaseBill? = cache.get()

    fun preloadAd() {
        Log.d("AdManager", "preload: start preload ad type: $type")
        loadAd(null, 1, null)
    }

    fun preloadAd(position: ViBillPosition) {
        Log.d("AdManager", "preload: start preload ad pos: ${position.position}")
        loadAd(position, 1)
    }

    fun loadAd(
        position: ViBillPosition,
        loadType: Int = 0,
        callback: ((ViBaseBill?) -> Unit)? = null
    ) {
        if (loadType != 1)
            Log.d("AdManager", "load: start load ad pos: ${position.position}")
        if (!ViPositionHelper.openBillPosition(position)) {
            Log.d("AdManager", "load: ad position close pos: ${position.position}")
            callback?.invoke(null)
            return
        }
        loadAd(position.fillInType, loadType, callback)
    }

    fun loadBannerAd(
        activity: Activity,
        position: ViBillPosition,
        callback: ((ViBaseBill?) -> Unit)
    ) {
        Log.d("AdManager", "load: start load ad pos: ${position.position}")
        if (!ViPositionHelper.openBillPosition(position)) {
            Log.d("AdManager", "load: ad position close pos: ${position.position}")
            callback.invoke(null)
            return
        }
        val billInfo = getBillInfo()
        if (billInfo == null) {
            Log.d("AdManager", "load: ad config is null type: $type")
            callback.invoke(null)
            return
        }
        loadAd(billInfo.keyInfoList, 0, activity) {
            callback.invoke(it)
        }

    }

    private fun loadAd(fillInType: ViBillType?, loadType: Int, callback: ((ViBaseBill?) -> Unit)?) {
        var comCallback = callback
        var count = 0
        if (loadType != 1) {
            val ad = cache.get()
            if (ad != null) {
                Log.d("AdManager", "load: has cache ad type: $type")
                comCallback?.invoke(ad)
                count = 1
                comCallback = null
            }
        }
        val billInfo = getBillInfo()
        if (billInfo == null) {
            Log.d("AdManager", "load: ad config is null type: $type")
            comCallback?.invoke(null)
            if (loadType == 1) {
                fillInType?.let {
                    if (it.type != type) {
                        it.preloadAd()
                    }
                }
            }
            return
        }
        if (isLoadingAd(billInfo, count)) {
            Log.d("AdManager", "load: limit max type: $type")
            if (comCallback != null) {
                completeCallback?.invoke(null)
                completeCallback = comCallback
            }
            return
        }
        loadingCount++
        if (comCallback != null) {
            completeCallback?.invoke(null)
            completeCallback = comCallback
        }
        loadAd(billInfo.keyInfoList, 0, null) {
            cache.add(it)
            loadingCount--
            completeCallback?.invoke(it)
            completeCallback = null
            checkError(it != null)
        }
    }

    private fun checkError(isSuccess: Boolean) {
        if (isSuccess) {
            errorCount = 0
            errorJob?.cancel()
            return
        }
        errorCount++
        val time = (2.0.pow(errorCount) * DateUtils.SECOND_IN_MILLIS).toLong()
        errorJob = SmartApp.instance.scope.launch {
            if (time > 0) delay(time)
            withContext(Dispatchers.Main) {
                preloadAd()
            }
        }
    }

    private fun loadAd(
        keyList: MutableList<ViKeyInfo>,
        index: Int,
        activity: Activity?,
        callback: (ViBaseBill?) -> Unit
    ) {
        if (index >= keyList.size) {
            Log.d("AdManager", "load: loaded all config type: $type")
            callback.invoke(null)
            return
        }
        val keyInfo = keyList[index]
        when (type) {
            TYPE_INTERSTITIAL.type -> {
                InterstitialAd.load(
                    SmartApp.instance,
                    keyInfo.key,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            Log.d("AdManager", "load: load ad fail type: $type msg: ${p0.message}")
                            loadAd(keyList, index + 1, activity, callback)
                        }

                        override fun onAdLoaded(p0: InterstitialAd) {
                            super.onAdLoaded(p0)
                            Log.d("AdManager", "load: load ad success type: $type")
                            callback.invoke(ViInterstitialInfo(keyInfo, p0))
                        }
                    })
            }

            TYPE_NATIVE.type -> {
                var native: ViNativeInfo? = null
                AdLoader.Builder(SmartApp.instance, keyInfo.key).forNativeAd {
                    Log.d("AdManager", "load: load ad success type: $type")
                    native = ViNativeInfo(keyInfo, it)
                    callback.invoke(native)
                }.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        Log.d("AdManager", "load: load ad fail type: $type msg: ${p0.message}")
                        loadAd(keyList, index + 1, activity, callback)
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        native?.onClick()
                    }

                }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
                    .loadAd(AdRequest.Builder().build())
            }

            TYPE_OPEN.type -> {
                AppOpenAd.load(
                    SmartApp.instance,
                    keyInfo.key,
                    AdRequest.Builder().build(),
                    object : AppOpenAdLoadCallback() {
                        override fun onAdLoaded(p0: AppOpenAd) {
                            super.onAdLoaded(p0)
                            Log.d("AdManager", "load: load ad success type: $type")
                            callback.invoke(ViOpenInfo(keyInfo, p0))
                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            Log.d("AdManager", "load: load ad fail type: $type msg: ${p0.message}")
                            loadAd(keyList, index + 1, activity, callback)
                        }
                    })
            }

            else -> {
                val adView = AdView(activity ?: SmartApp.instance)
                val adSize = getBannerSeize(activity)
                adView.setAdSize(adSize)
                adView.adUnitId = keyInfo.key
                adView.adListener = object : AdListener() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        Log.d("AdManager", "load: load ad fail type: $type msg: ${p0.message}")
                        loadAd(keyList, index + 1, activity, callback)
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Log.d("AdManager", "load: load ad success type: $type")
                        callback.invoke(ViBannerInfo(keyInfo, adView).apply {
                            bannerH = adSize.height.toFloat().dpToPx().toInt()
                        })
                    }
                }
                adView.loadAd(
                    AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, Bundle().apply {
                            putString("collapsible", "bottom")
                        }).build()
                )
            }
        }
    }

    private fun getBannerSeize(activity: Activity?): AdSize {
        try {
            val manager = if (activity != null) {
                activity.windowManager
            } else {
                SmartApp.instance.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            }
            if (manager == null) return AdSize.BANNER
            val displayMetrics = DisplayMetrics()
            manager.defaultDisplay.getMetrics(displayMetrics)
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                activity ?: SmartApp.instance,
                (displayMetrics.widthPixels / displayMetrics.density).toInt()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return AdSize.BANNER
    }

    private fun isLoadingAd(billInfo: ViBillInfo, count: Int = 0): Boolean {
        return billInfo.limit < (cache.size() + loadingCount - count)
    }

    private var billInfo: ViBillInfo? = null

    private fun getBillInfo(): ViBillInfo? {
        if (billInfo != null) return billInfo
        val config = Utils.decodeBase64(FireRemoteConf.instance.adConfig)
        if (config.isEmpty()) return null
        try {
            val json = JSONObject(config).optJSONObject(type) ?: return null
            val array = json.optJSONArray("sq_ky_list") ?: return null
            val count = array.length()
            if (count <= 0) return null
            val limit = json.getInt("sq_mx_c")
            val keyList = mutableListOf<ViKeyInfo>()
            for (n in 0 until count) {
                array.getJSONObject(n).run {
                    keyList.add(ViKeyInfo(getString("sq_ky"), getInt("sq_pr")))
                }
            }
            keyList.sortByDescending { it.priority }
            billInfo = ViBillInfo(limit, keyList)
            return billInfo
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}