package com.szr.co.smart.qr.bill.info

import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.szr.co.smart.qr.bill.type.ViBillType
import com.szr.co.smart.qr.activity.base.BaseActivity

class ViInterstitialInfo(keyInfo: ViKeyInfo, ad: Any) : ViBaseBill(keyInfo, ad) {

    override val billType: ViBillType = ViBillType.TYPE_INTERSTITIAL

    override fun startShow(activity: BaseActivity<*>) {
        isShowed = true
        if (ad !is InterstitialAd) {
            Log.d("AdManager", "show: ad type error type: ${billType.type}")
            dismiss()
            return
        }
        ad.setOnPaidEventListener {
            eventAdValue(it, ad.responseInfo.loadedAdapterResponseInfo?.adSourceName)
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                Log.d("AdManager", "show: show ad fail type: ${billType.type} msg: ${p0.message}")
                dismiss()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                Log.d("AdManager", "show: close ad type: ${billType.type}")
                dismiss()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                Log.d("AdManager", "show: show ad success type: ${billType.type}")
                showSuccess()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d("AdManager", "show: click ad type: ${billType.type}")
                click()
            }
        }
        ad.show(activity)
        show()

    }

}