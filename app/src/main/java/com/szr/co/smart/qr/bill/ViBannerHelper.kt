package com.szr.co.smart.qr.bill

import android.view.View
import android.view.ViewGroup
import com.szr.co.smart.qr.bill.cache.ViBillCache
import com.szr.co.smart.qr.bill.callbacks.ViOnBillCallback
import com.szr.co.smart.qr.bill.info.ViBannerInfo
import com.szr.co.smart.qr.bill.info.ViBaseBill
import com.szr.co.smart.qr.bill.position.ViBillPosition
import com.szr.co.smart.qr.bill.position.ViPositionHelper
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.event.AppAdTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViBannerHelper(
    private val activity: BaseActivity<*>,
    private val position: ViBillPosition,
    private val bannerLayout: ViewGroup,
    private val bannerPlaceView: View
) {

    private val cache = ViBillCache()

    private var loadBannerJob: Job? = null

    private var isShowed = false

    fun onResume() {
        if (isShowed && !ViPositionHelper.refreshBillPosition(position)) return
        loadBannerJob?.cancel()
        loadBannerJob = SmartApp.instance.scope.launch {
            delay(210)
            AppAdTrack.logAdEnter(position.position)
            withContext(Dispatchers.Main) {
                loadAd {
                    show(it)
                }
            }
        }
    }

    private var complete: ((ViBaseBill?) -> Unit)? = null
    private var isLoading = false

    private fun loadAd(complete: ((ViBaseBill?) -> Unit)?) {
        val ad = cache.get()
        if (ad != null) {
            complete?.invoke(ad)
            return
        }
        if (isLoading) {
            if (complete != null)
                this.complete = complete
            return
        }
        isLoading = true
        this.complete = complete
        position.type.loadBannerAd(activity, position) {
            cache.add(it)
            isLoading = false
            this.complete?.invoke(it)
        }
    }

    private fun preload() {
        loadAd(null)
    }

    private fun show(bill: ViBaseBill?) {
        if (bill == null) return
        if (bill is ViBannerInfo) {
            bill.setBannerGroup(bannerLayout).setOnBillCallback(object : ViOnBillCallback {
                override fun onShowSuccess() {
                    super.onShowSuccess()
                    isShowed = true
                    bannerPlaceView.run {
                        val lp = layoutParams
                        lp.height = bill.bannerH
                        layoutParams = lp
                    }
                }
            }).show(activity, position.position)
            if (ViPositionHelper.refreshBillPosition(position)) {
                preload()
            }
        }
    }

    fun onPause() {
        loadBannerJob?.cancel()
        loadBannerJob = null
    }

}