package com.szr.co.smart.qr.bill.info

import android.util.Log
import android.view.ViewGroup
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.szr.co.smart.qr.bill.type.ViBillType
import com.szr.co.smart.qr.activity.base.BaseActivity

class ViBannerInfo(keyInfo: ViKeyInfo, ad: Any) : ViBaseBill(keyInfo, ad) {

    var bannerH = 0

    private var bannerGroup: ViewGroup? = null

    fun setBannerGroup(group: ViewGroup): ViBannerInfo {
        bannerGroup = group
        return this
    }

    override val billType: ViBillType = ViBillType.TYPE_BANNER

    override fun startShow(activity: BaseActivity<*>) {
        if (bannerGroup == null) {
            Log.d("AdManager", "show: bannerGroup is null")
            dismiss()
            return
        }
        isShowed = true
        if (ad !is AdView) {
            Log.d("AdManager", "show: ad type error type: ${billType.type}")
            dismiss()
            return
        }
        ad.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                Log.d("AdManager", "show: close ad type: ${billType.type}")
                dismiss()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d("AdManager", "show: click ad type: ${billType.type}")
                click()
            }
        }
        ad.setOnPaidEventListener {
            eventAdValue(it, ad.responseInfo?.loadedAdapterResponseInfo?.adSourceName)
        }
        bannerGroup!!.removeAllViews()
        bannerGroup!!.addView(ad)
        show()
        showSuccess()
    }

}