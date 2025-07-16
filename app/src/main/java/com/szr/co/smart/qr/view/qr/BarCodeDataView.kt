package com.szr.co.smart.qr.view.qr

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.LayoutBarcodeViewBinding
import com.szr.co.smart.qr.model.QRCodeType
import com.szr.co.smart.qr.utils.toast

class BarCodeDataView : BaseQRDataView {
    private var mBinding: LayoutBarcodeViewBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        mBinding = LayoutBarcodeViewBinding.inflate(LayoutInflater.from(context), this, true)

        mBinding.etData.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                if (maxLength == -1) return
                val count = s?.count() ?: 0
                mBinding.tvCount.text = "$count/$maxLength"
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private var maxLength = -1
    private var type: Int? = null

    @SuppressLint("SetTextI18n")
    fun setType(type: Int) {
        this.type = type
        mBinding.tvCount.isVisible = true
        when (type) {
            QRCodeType.BARCODE_ITF -> {
                mBinding.tvCount.isVisible = false
                mBinding.etData.setHint(R.string.event_number)

                mBinding.etData.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            }

            QRCodeType.BARCODE_CODABAR -> {
                mBinding.tvCount.isVisible = false
                mBinding.etData.setHint(R.string.digits)
                mBinding.etData.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            }

            QRCodeType.BARCODE_CODE_39 -> {
                mBinding.tvCount.isVisible = false
                mBinding.etData.setHint(R.string.uppercase_text_without_special_symbols)

                mBinding.etData.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                mBinding.etData.setKeyListener(DigitsKeyListener.getInstance("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));
            }

            QRCodeType.BARCODE_CODE_93 -> {
                mBinding.tvCount.isVisible = false
                mBinding.etData.setHint(R.string.uppercase_text_without_special_symbols)
                val allowedChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%"
                mBinding.etData.setKeyListener(DigitsKeyListener.getInstance(allowedChars))
            }

            QRCodeType.BARCODE_CODE_128 -> {
                mBinding.tvCount.isVisible = false
                mBinding.etData.setHint(R.string.text_without_special_symbols)
            }

            QRCodeType.BARCODE_UPC_A -> {
                mBinding.etData.setHint(R.string.upc_a_hint)
                maxLength = 12
                mBinding.etData.filters=  arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                mBinding.tvCount.text = "0/12"
                val allowedChars = "0123456789"
                mBinding.etData.setKeyListener(DigitsKeyListener.getInstance(allowedChars))
            }

            QRCodeType.BARCODE_UPC_E -> {
                mBinding.etData.setHint(R.string.upc_e_hint)
                maxLength = 8
                mBinding.etData.filters=  arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                mBinding.tvCount.text = "0/8"

                val allowedChars = "0123456789"
                mBinding.etData.setKeyListener(DigitsKeyListener.getInstance(allowedChars))
            }

            QRCodeType.BARCODE_EAN_8 -> {
                mBinding.etData.setHint(R.string.ean_8_hint)
                maxLength = 8
                mBinding.etData.filters=  arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                mBinding.tvCount.text = "0/8"

                val allowedChars = "0123456789"
                mBinding.etData.setKeyListener(DigitsKeyListener.getInstance(allowedChars))
            }

            QRCodeType.BARCODE_EAN_13 -> {
                mBinding.etData.setHint(R.string.ean_13_hint)
                maxLength = 13
                mBinding.etData.filters=  arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                mBinding.tvCount.text = "0/13"
                val allowedChars = "0123456789"
                mBinding.etData.setKeyListener(DigitsKeyListener.getInstance(allowedChars))
            }
        }
    }

    override fun qrData(): String {
        val data = mBinding.etData.text.toString()
        when (type) {
            QRCodeType.BARCODE_ITF -> {
                if (data.length > 25) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }


            QRCodeType.BARCODE_CODE_39 -> {

            }

            QRCodeType.BARCODE_CODE_93 -> {

            }

            QRCodeType.BARCODE_CODE_128 -> {

            }

            QRCodeType.BARCODE_UPC_A -> {
                if (data.length > 12) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }

            }

            QRCodeType.BARCODE_UPC_E -> {
                if (data.length > 8) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }

            QRCodeType.BARCODE_EAN_8 -> {
                if (data.length > 8) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }

            QRCodeType.BARCODE_EAN_13 -> {
                if (data.length > 13) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }
        }
        return data
    }

}