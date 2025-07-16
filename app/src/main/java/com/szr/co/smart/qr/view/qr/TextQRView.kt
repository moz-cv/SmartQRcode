package com.szr.co.smart.qr.view.qr

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.szr.co.smart.qr.databinding.LayoutQrcodeTextBinding
import com.szr.co.smart.qr.databinding.LayoutQrcodeWebsiteBinding

class TextQRView :  BaseQRDataView {

    private var mBinding: LayoutQrcodeTextBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        mBinding = LayoutQrcodeTextBinding.inflate(LayoutInflater.from(context), this, true)


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

    override fun qrData(): String {
        return mBinding.etUrl.text.toString()
    }

}