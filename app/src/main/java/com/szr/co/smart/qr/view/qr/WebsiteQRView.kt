package com.szr.co.smart.qr.view.qr

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.LayoutQrcodeWebsiteBinding
import com.szr.co.smart.qr.utils.ValidateUtils
import com.szr.co.smart.qr.utils.toast

class WebsiteQRView :  BaseQRDataView {

    private var mBinding: LayoutQrcodeWebsiteBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        mBinding = LayoutQrcodeWebsiteBinding.inflate(LayoutInflater.from(context), this, true)
        mBinding.tvHttp.setOnClickListener { insertType(1) }
        mBinding.tvHttps.setOnClickListener { insertType(0) }
        mBinding.tvCom.setOnClickListener { insertType(3) }
        mBinding.tvWww.setOnClickListener { insertType(2) }

        mBinding.etUrl.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val count = s?.count() ?: 0
                mBinding.tvCount.text = "$count/500"
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun insertType(type: Int) {
        val str = mBinding.etUrl.text.toString()
        val typeStr = when (type) {
            0 -> "https://$str"
            1 -> "http://$str"
            2 -> "www.$str"
            3 -> "$str.com"
            else -> ""
        }
        if (typeStr.isEmpty()) return
        mBinding.etUrl.setText(typeStr)

    }


    override fun qrData(): String {
        val url = mBinding.etUrl.text.toString().trim()
        if (url.isEmpty()) {
            context.toast(context.getString(R.string.url_not_empty))
            return "none"
        }
        if (!ValidateUtils.isValidWebsite(url)) {
            context.toast(context.getString(R.string.url_invalidate))
            return "none"
        }
        return url
    }

}