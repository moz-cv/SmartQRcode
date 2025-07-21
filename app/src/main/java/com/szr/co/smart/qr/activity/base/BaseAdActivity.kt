package com.szr.co.smart.qr.activity.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.szr.co.smart.qr.bill.ViBillHelper
import com.szr.co.smart.qr.bill.position.ViBillPosition

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
}