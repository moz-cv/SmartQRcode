package com.szr.co.smart.qr.view.qr

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.LayoutQrcodeContactBinding
import com.szr.co.smart.qr.databinding.LayoutQrcodeSingleEditBinding
import com.szr.co.smart.qr.databinding.LayoutQrcodeWebsiteBinding
import com.szr.co.smart.qr.databinding.LayoutQrcodeWifiBinding
import com.szr.co.smart.qr.model.QRCodeType
import com.szr.co.smart.qr.utils.QrUtils
import com.szr.co.smart.qr.utils.ValidateUtils
import com.szr.co.smart.qr.utils.toast

class SingleEditQRView : BaseQRDataView {

    private var mBinding: LayoutQrcodeSingleEditBinding
    private var type: Int? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        mBinding = LayoutQrcodeSingleEditBinding.inflate(LayoutInflater.from(context), this, true)
    }


    fun setType(@QRCodeType type: Int) {
        this.type = type
        val title = when (type) {
            QRCodeType.QRCODE_PHONE -> R.string.phone
            QRCodeType.QRCODE_EMAIL -> R.string.email
            QRCodeType.QRCODE_WHATSAPP -> {
                mBinding.etText.setHint(R.string.please_phone_number)
                mBinding.etText.setRawInputType(InputType.TYPE_CLASS_PHONE)
                R.string.whatsapp
            }

            QRCodeType.QRCODE_INS -> {
                mBinding.etText.setHint(R.string.please_url)
                R.string.ins
            }

            QRCodeType.QRCODE_X -> {
                mBinding.etText.setHint(R.string.please_url)
                R.string.x
            }

            QRCodeType.QRCODE_FACEBOOK -> {
                mBinding.etText.setHint(R.string.please_url)
                R.string.facebook
            }

            else -> -1
        }

        if (title == -1) return
        mBinding.tvTitle.setText(title)

    }

    override fun qrData(): String {
        if (type == null) return ""
        var data = mBinding.etText.text.toString()
        when (type) {
            QRCodeType.QRCODE_PHONE -> {
                if (!ValidateUtils.isValidPhone(data)) {
                    context.toast(context.getString(R.string.phone_invalidate))
                    return "none"
                }

            }

            QRCodeType.QRCODE_EMAIL -> {
                if (!ValidateUtils.isValidEmail(data)) {
                    context.toast(context.getString(R.string.email_invalidate))
                    return "none"
                }
            }

            QRCodeType.QRCODE_WHATSAPP -> {
//                if (!QrUtils.isWhatsApp(data)) {
//                    context.toast(context.getString(R.string.whatsapp_invalidate))
//                    return "none"
//                }

                if (data.isEmpty()) {
                    context.toast(context.getString(R.string.whatsapp_invalidate))
                    return "none"
                }
                data = "https://wa.me/$data"
            }

            QRCodeType.QRCODE_INS -> {
                if (!QrUtils.isInstagram(data)) {
                    context.toast(context.getString(R.string.ins_invalidate))
                    return "none"
                }
            }

            QRCodeType.QRCODE_X -> {
                if (!QrUtils.isTwitter(data)) {
                    context.toast(context.getString(R.string.x_invalidate))
                    return ""
                }
            }

            QRCodeType.QRCODE_FACEBOOK -> {
                if (!QrUtils.isFacebook(data)) {
                    context.toast(context.getString(R.string.facebook_invalidate))
                    return "none"
                }
            }

            else -> {
                return "none"
            }
        }
        return data
    }

}