package com.szr.co.smart.qr.bill.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.protobuf.type
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.NativeAd2Binding
import com.szr.co.smart.qr.databinding.NativeAdBigBinding
import com.szr.co.smart.qr.databinding.NativeAdBinding
import com.szr.co.smart.qr.manager.UserManager

class GradNativeAdLayout : FrameLayout {

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
        init(attr)
    }

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context, attr, defStyleAttr
    ) {
        init(attr)
    }

    private var nativeAd: NativeAd? = null
    private var adCls = 1
    private var descColor = -1

    private fun init(attr: AttributeSet?) {
        attr?.let {
            val typed = resources.obtainAttributes(it, R.styleable.SmartNativeAdLayout)
            adCls = typed.getInt(R.styleable.SmartNativeAdLayout_ad_type, 1)
            typed.recycle()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        nativeAd?.destroy()
        nativeAd = null
    }

    fun adViewContainer(): NativeAdView {
        return if (UserManager.instance.buyUser()) {
            if (adCls == 1) {
                createAdViewBig()
            } else {
                createAdViewNormal2()
            }
        } else {
            createAdViewNormal()
        }
    }

    fun showAd(nativeAd: NativeAd) {
        this.nativeAd?.destroy()
        this.nativeAd = nativeAd
        val adView = adViewContainer()
        (adView.headlineView as TextView).apply {
            this.text = nativeAd.headline
            if (descColor != -1) this.setTextColor(descColor)
        }
        adView.bodyView?.visibility = INVISIBLE
        adView.callToActionView?.visibility = INVISIBLE
        adView.iconView?.visibility = GONE
        nativeAd.body?.let {
            adView.bodyView?.visibility = VISIBLE
            (adView.bodyView as TextView).apply {
                this.text = it
                if (descColor != -1) this.setTextColor(descColor)
            }
        }
        nativeAd.callToAction?.let {
            adView.callToActionView?.visibility = VISIBLE
            (adView.callToActionView as TextView).text = it
//            adView.callToActionView?.setBackgroundResource(if (UserTypeManger.instance.isChannelUser()) R.drawable.vibe_ad_btn else R.drawable.vibe_ad_btn1)
        }
        nativeAd.icon?.let {
            adView.iconView?.visibility = VISIBLE
            (adView.iconView as ImageView).setImageDrawable(it.drawable)
        }
        nativeAd.mediaContent?.let {
            adView.mediaView?.setMediaContent(it)
        }
        adView.setNativeAd(nativeAd)
        removeAllViews()
        addView(adView)
    }


    private fun createAdViewBig(): NativeAdView {
        NativeAdBigBinding.inflate(LayoutInflater.from(context)).run {
            adView.headlineView = adTitle
            adView.bodyView = adDescription
            adView.mediaView = adMedia
            adView.callToActionView = adAction
            adView.iconView = adIcon
            adView.adChoicesView = adChoi
            return adView
        }
    }

    private fun createAdViewNormal(): NativeAdView {
        NativeAdBinding.inflate(LayoutInflater.from(context)).run {
            adView.headlineView = adTitle
            adView.bodyView = adDescription
            adView.mediaView = adMedia
            adView.callToActionView = adAction
            adView.iconView = adIcon
            return adView
        }
    }

    private fun createAdViewNormal2(): NativeAdView {
        NativeAd2Binding.inflate(LayoutInflater.from(context)).run {
            adView.headlineView = adTitle
            adView.bodyView = adDescription
            adView.mediaView = adMedia
            adView.callToActionView = adAction
            adView.iconView = adIcon
            return adView
        }
    }
}