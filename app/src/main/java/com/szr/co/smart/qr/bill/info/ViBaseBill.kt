package com.szr.co.smart.qr.bill.info

import android.text.format.DateUtils
import android.util.Log
import com.google.android.gms.ads.AdValue
import com.szr.co.smart.qr.bill.type.ViBillType
import com.szr.co.smart.qr.bill.callbacks.ViOnBillCallback
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.event.AppAdTrack
import kotlin.math.abs

abstract class ViBaseBill(val keyInfo: ViKeyInfo, val ad: Any) {

    private val time = System.currentTimeMillis()

    private var callback: ViOnBillCallback? = null

    private var showPos: String? = null

    protected var isShowed = false

    private var adValue: AdValue? = null

    val isCanShow: Boolean
        get() {
            if (isShowed) return false
            return abs(System.currentTimeMillis() - time) < DateUtils.HOUR_IN_MILLIS
        }

    protected abstract fun startShow(activity: BaseActivity<*>)

    protected abstract val billType: ViBillType

    fun setOnBillCallback(callback: ViOnBillCallback): ViBaseBill {
        this.callback = callback
        return this
    }

    fun show(activity: BaseActivity<*>, pos: String) {
        Log.d("AdManager", "show: start show ad type: ${billType.type}")
        if (!activity.isShowing) {
            callback?.onDismiss()
            Log.d("AdManager", "show: activity is pause type: ${billType.type}")
            return
        }
        showPos = pos
        startShow(activity)
    }

    private fun eventClickAdValue(value: AdValue?) {
        if (value == null) return
        AppAdTrack.fbAdClick(value.valueMicros, value.currencyCode)
    }

    protected fun eventAdValue(value: AdValue, sourceName: String?) {
        this.adValue = value
        val type = when (billType.type) {
            ViBillType.TYPE_BANNER.type -> 5
            ViBillType.TYPE_OPEN.type -> 2
            ViBillType.TYPE_NATIVE.type -> 6
            else -> 3
        }
        AppAdTrack.adPaid(
            sourceName ?: "",
            type,
            keyInfo.key,
            value.valueMicros,
            value.currencyCode
        )
    }

    protected fun dismiss() {
        callback?.onDismiss()
    }

    protected fun show() {
        if (billType.type != ViBillType.TYPE_BANNER.type) {
            billType.preloadAd()
        }
        showPos?.run {
            AppAdTrack.logAdShow(this)
        }

    }

    protected fun showSuccess() {
        callback?.onShowSuccess()
        showPos?.run {
            AppAdTrack.logAdShowSuc(this)
        }
    }

    protected fun click() {
        showPos?.run {
            AppAdTrack.logAdClick(this)
        }
        eventClickAdValue(adValue)
    }

}