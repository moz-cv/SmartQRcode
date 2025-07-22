package com.szr.co.smart.qr.bill

import android.util.Log
import androidx.core.view.isVisible
import com.szr.co.smart.qr.bill.callbacks.ViOnBillCallback
import com.szr.co.smart.qr.bill.info.ViBaseBill
import com.szr.co.smart.qr.bill.info.ViNativeInfo
import com.szr.co.smart.qr.bill.position.ViBillPosition
import com.szr.co.smart.qr.bill.position.ViPositionHelper
import com.szr.co.smart.qr.bill.view.SmartNativeAdLayout
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.event.AppAdTrack

class ViBillHelper(
    private val activity: BaseActivity<*>,
    val showIntersPos: ViBillPosition?,
    private val preloadPosList: MutableList<ViBillPosition>?,
    private val showNativePos: ViBillPosition? = null,
    var nativeAdLayout: SmartNativeAdLayout? = null,
    private val isResume: Boolean = false
) {

    var skipAdOnce = false

    fun onCreate() {
        if (!isResume) preload()
        nativeAdLayout?.isVisible =
            (showNativePos != null && ViPositionHelper.openBillPosition(showNativePos))
    }

    private fun preload() {
        preloadPosList?.run {
            forEach {
                it.type.preloadAd(it)
            }
        }
        showIntersPos?.run {
            type.preloadAd(this)
        }
    }

    fun onResume() {
        if (isResume) preload()
        if (skipAdOnce) {
            skipAdOnce = false
            return
        }
        if (showNativePos == null || nativeAdLayout == null) return
        AppAdTrack.logAdEnter(showNativePos.position)
        showNativePos.type.loadAd(showNativePos) {
            if (it == null) return@loadAd
            (it as ViNativeInfo).setNativeAdLayout(nativeAdLayout!!)
                .show(activity, showNativePos.position)
        }
    }

    fun onPause() {

    }

    fun loadAd(position: ViBillPosition, complete: (ViBaseBill?) -> Unit) {
        if (showIntersPos == null) {
            complete.invoke(null)
            return
        }
        showIntersPos.type.loadAd(position, callback = complete)
    }

    fun loadAd(complete: (ViBaseBill?) -> Unit) {
        if (showIntersPos == null) {
            complete.invoke(null)
            return
        }
        showIntersPos.type.loadAd(showIntersPos, callback = complete)
    }

    fun showAd(bill: ViBaseBill?, dismiss: () -> Unit) {
        if (showIntersPos == null) {
            dismiss.invoke()
            return
        }
        if (bill == null) {
            Log.d("AdManager", "show: ad is null pos: ${showIntersPos.position}")
            dismiss.invoke()
            return
        }
        bill.setOnBillCallback(object : ViOnBillCallback {
            override fun onDismiss() {
                super.onDismiss()
                dismiss.invoke()
            }
        }).show(activity, showIntersPos.position)
    }

    fun showAd(dismiss: () -> Unit) {
        if (showIntersPos == null) {
            dismiss.invoke()
            return
        }
        AppAdTrack.logAdEnter(showIntersPos.position)
        showAd(ViPositionHelper.getBill(showIntersPos), dismiss)
    }


    fun showAd(position: ViBillPosition, dismiss: () -> Unit) {
        if (showIntersPos == null) {
            dismiss.invoke()
            return
        }
        AppAdTrack.logAdEnter(position.position)
        showAd(ViPositionHelper.getBill(position), dismiss)
    }

    fun onBack(end: () -> Unit) {
        AppAdTrack.logAdEnter(ViBillPosition.POS_RETURN_INTERS.position)
        val bill = ViPositionHelper.getBill(ViBillPosition.POS_RETURN_INTERS)
        if (bill == null) {
            end.invoke()
            return
        }
        bill.setOnBillCallback(object : ViOnBillCallback {
            override fun onDismiss() {
                super.onDismiss()
                end.invoke()
            }
        }).show(activity, ViBillPosition.POS_RETURN_INTERS.position)
    }


}