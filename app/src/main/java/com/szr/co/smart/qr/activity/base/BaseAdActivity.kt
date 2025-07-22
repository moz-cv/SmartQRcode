package com.szr.co.smart.qr.activity.base

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.szr.co.smart.qr.bill.ViBillHelper
import com.szr.co.smart.qr.bill.position.ViBillPosition
import com.szr.co.smart.qr.bill.position.ViPositionHelper
import com.szr.co.smart.qr.dialog.AdWaitingDialog
import com.szr.co.smart.qr.event.AppAdTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseAdActivity<T : ViewBinding> : BaseActivity<T>() {

    protected open val showBackAd: Boolean = false

    abstract val billHelper: ViBillHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billHelper.onCreate()
        if (showBackAd) {
            ViBillPosition.POS_RETURN_INTERS.run {
                type.preloadAd(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        billHelper.onResume()
    }

    override fun onPause() {
        super.onPause()
        billHelper.onPause()
    }


    override fun onAppBackPage() {
        if (showBackAd) {
            billHelper.onBack {
                super.onAppBackPage()
            }
        } else {
            super.onAppBackPage()
        }
    }

    private var mLoadAdDialog: AdWaitingDialog? = null
    fun showAdDelayLoad(dismiss: () -> Unit) {
        val position = billHelper.showIntersPos
        if (position == null) {
            dismiss.invoke()
            return
        }
        AppAdTrack.logAdEnter(position.position)
        val cacheAd = ViPositionHelper.getBill(position)

        if (cacheAd == null) {
            mLoadAdDialog = AdWaitingDialog(this)
            mLoadAdDialog?.show()
            loadAdDelayJob = lifecycleScope.launch {
                withContext(Dispatchers.Default) { delay(5000) }
                mLoadAdDialog?.dismiss()
                mLoadAdDialog = null
                billHelper.showAd(ViPositionHelper.getBill(position), dismiss)
            }
            loadAdDelayJob = lifecycleScope.launch {

            }
            billHelper.loadAd(position) {
                loadAdDelayJob?.cancel()
                loadAdDelayJob = null
                if (mLoadAdDialog == null) return@loadAd
                mLoadAdDialog?.dismiss()
                mLoadAdDialog = null
                billHelper.showAd(it, dismiss)
            }
        } else {
            billHelper.showAd(ViPositionHelper.getBill(position), dismiss)
        }

    }

    private var loadAdDelayJob: Job? = null

    override fun onDestroy() {
        super.onDestroy()
        loadAdDelayJob?.cancel()
        loadAdDelayJob = null
        mLoadAdDialog?.dismiss()
        mLoadAdDialog = null
    }
}