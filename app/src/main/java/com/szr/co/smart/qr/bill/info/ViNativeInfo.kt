package com.szr.co.smart.qr.bill.info

import android.util.Log
import com.google.android.gms.ads.nativead.NativeAd
import com.szr.co.smart.qr.bill.type.ViBillType
import com.szr.co.smart.qr.bill.view.SmartNativeAdLayout
import com.szr.co.smart.qr.activity.base.BaseActivity

class ViNativeInfo(keyInfo: ViKeyInfo, ad: Any) : ViBaseBill(keyInfo, ad) {

    private var nativeAdLayout: SmartNativeAdLayout? = null

    fun onClick() {
        click()
    }

    fun setNativeAdLayout(nativeAdLayout: SmartNativeAdLayout): ViNativeInfo {
        this.nativeAdLayout = nativeAdLayout
        return this
    }

    override val billType: ViBillType = ViBillType.TYPE_NATIVE

    override fun startShow(activity: BaseActivity<*>) {
        if (nativeAdLayout == null) {
            Log.d("AdManager", "show: nativeAdLayout is null")
            dismiss()
            return
        }
        isShowed = true
        if (ad !is NativeAd) {
            Log.d("AdManager", "show: ad type error type: ${billType.type}")
            dismiss()
            return
        }
        ad.setOnPaidEventListener {
            eventAdValue(it, ad.responseInfo?.loadedAdapterResponseInfo?.adSourceName)
        }
        nativeAdLayout!!.showAd(ad)
        show()
        showSuccess()
    }

}