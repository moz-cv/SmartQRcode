package com.szr.co.smart.qr.view.qr

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.LayoutBarcodeViewBinding
import com.szr.co.smart.qr.model.QRCodeType
import com.szr.co.smart.qr.utils.toast
import java.util.Locale

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
                mBinding.etData.apply {

                    // 这样设置会弹出全键盘但限制输入内容
                    keyListener = object : DigitsKeyListener(Locale.getDefault()) {
                        override fun getInputType() = InputType.TYPE_CLASS_TEXT // 关键修改

                        override fun filter(
                            source: CharSequence,
                            start: Int,
                            end: Int,
                            dest: Spanned,
                            dstart: Int,
                            dend: Int
                        ): CharSequence {
                            val allowed =
                                "0123456789, -\\$:/.+ABCD"
                            return source.filter { allowed.contains(it) }
                        }
                    }
                }
            }

            QRCodeType.BARCODE_CODE_39 -> {
                mBinding.tvCount.isVisible = false
                mBinding.etData.setHint(R.string.uppercase_text_without_special_symbols)
                mBinding.etData.apply {

                    // 这样设置会弹出全键盘但限制输入内容
                    keyListener = object : DigitsKeyListener(Locale.getDefault()) {
                        override fun getInputType() = InputType.TYPE_CLASS_TEXT // 关键修改

                        override fun filter(
                            source: CharSequence,
                            start: Int,
                            end: Int,
                            dest: Spanned,
                            dstart: Int,
                            dend: Int
                        ): CharSequence {
                            val allowed =
                                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-.$/+% "
                            return source.filter { allowed.contains(it) }
                        }
                    }
                }
            }

            QRCodeType.BARCODE_CODE_93 -> {
                mBinding.tvCount.isVisible = false
                mBinding.etData.setHint(R.string.uppercase_text_without_special_symbols)
                mBinding.etData.apply {
                    // 必须使用文本输入类型（不能使用TYPE_NUMBER）
                    inputType = InputType.TYPE_CLASS_TEXT

                    // 这样设置会弹出全键盘但限制输入内容
                    keyListener = object : DigitsKeyListener(Locale.getDefault()) {
                        override fun getInputType() = InputType.TYPE_CLASS_TEXT // 关键修改

                        override fun filter(
                            source: CharSequence,
                            start: Int,
                            end: Int,
                            dest: Spanned,
                            dstart: Int,
                            dend: Int
                        ): CharSequence {
                            val allowed = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%"
                            return source.filter { allowed.contains(it) }
                        }
                    }
                }
            }

            QRCodeType.BARCODE_CODE_128 -> {
                mBinding.tvCount.isVisible = false
                mBinding.etData.setHint(R.string.text_without_special_symbols)
            }

            QRCodeType.BARCODE_UPC_A -> {
                mBinding.etData.setHint(R.string.upc_a_hint)
                maxLength = 11
                mBinding.etData.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                mBinding.tvCount.text = "0/11"
                mBinding.etData.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            }

            QRCodeType.BARCODE_UPC_E -> {
                mBinding.etData.setHint(R.string.upc_e_hint)
                maxLength = 7
                mBinding.etData.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                mBinding.tvCount.text = "0/7"

                mBinding.etData.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            }

            QRCodeType.BARCODE_EAN_8 -> {
                mBinding.etData.setHint(R.string.ean_8_hint)
                maxLength = 7
                mBinding.etData.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                mBinding.tvCount.text = "0/7"

                mBinding.etData.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            }

            QRCodeType.BARCODE_EAN_13 -> {
                mBinding.etData.setHint(R.string.ean_13_hint)
                maxLength = 12
                mBinding.etData.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                mBinding.tvCount.text = "0/12"
                mBinding.etData.setRawInputType(InputType.TYPE_CLASS_NUMBER)
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
                // ITF（Interleaved 2 of 5）：仅支持偶数位数字（必须是偶数位，最少6位）
                if (!data.matches(Regex("^\\d{6,}$")) || data.length % 2 != 0) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }

            QRCodeType.BARCODE_CODABAR -> {
                // Codabar：仅支持 0-9, -$:/.+ABCD（必须以 A/B/C/D 开头和结尾
                val regex = "^[A-D][0-9\\-$:/.+]+[A-D]$".toRegex()
                if (!data.matches(regex)) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }

            }

            QRCodeType.BARCODE_CODE_39 -> {
                // 支持大写 A-Z, 数字 0-9 和符号 - . $ / + % 和空格
                val regex = "^[0-9A-Z\\-. $/+%]{1,43}$".toRegex()
                if (!data.matches(regex)) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }

            QRCodeType.BARCODE_CODE_93 -> {
                // CODE_93：支持 ASCII 字符，但 ZXing 默认仅支持 7-bit 范围
                if (data.length > 80) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }

            QRCodeType.BARCODE_CODE_128 -> {
                if (data.length > 80) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }

            QRCodeType.BARCODE_UPC_A -> {
                val regex = "^\\d{11}$".toRegex()
                if (!data.matches(regex)) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }

            QRCodeType.BARCODE_UPC_E -> {
                val regex = "^[01]\\d{6}$".toRegex()
                if (!data.matches(regex)) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }

            QRCodeType.BARCODE_EAN_8 -> {
                val regex = "^\\d{7}$".toRegex()
                if (!data.matches(regex)) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }

            QRCodeType.BARCODE_EAN_13 -> {
                val regex = "^\\d{12}$".toRegex()
                if (!data.matches(regex)) {
                    context.toast(context.getString(R.string.data_invalidate))
                    return "none"
                }
            }
        }
        return data
    }

}