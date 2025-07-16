package com.szr.co.smart.qr.view.qr

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.LayoutQrcodeContactBinding
import com.szr.co.smart.qr.databinding.LayoutQrcodeWebsiteBinding
import com.szr.co.smart.qr.databinding.LayoutQrcodeWifiBinding
import com.szr.co.smart.qr.utils.QrUtils
import com.szr.co.smart.qr.utils.toast
import com.szr.co.smart.qr.utils.ValidateUtils

class ContactQRView : BaseQRDataView {

    private var mBinding: LayoutQrcodeContactBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        mBinding = LayoutQrcodeContactBinding.inflate(LayoutInflater.from(context), this, true)
    }


    override fun qrData(): String {
        val name = mBinding.etName.text.toString().trim()
        val phone = mBinding.etPhoneNumber.text.toString().trim()
        val emailAddress = mBinding.etAddress.text.toString().trim()
        val birthday = mBinding.etBirthday.text.toString().trim()
        if (name.isEmpty()) {
            context.toast(context.getString(R.string.name_is_empty))
            return "none"
        }
        if (phone.isEmpty()) {
            context.toast(context.getString(R.string.phone_is_empty))
            return "none"
        }
        // 国际手机号校验
        if (!ValidateUtils.isValidPhone(phone)) {
            context.toast(context.getString(R.string.phone_invalidate))
            return "none"
        }
        // email校验
        if (emailAddress.isNotEmpty() && !ValidateUtils.isValidEmail(emailAddress)) {
            context.toast(context.getString(R.string.email_invalidate))
            return "none"
        }
        // vCard 2.1格式
        val sb = StringBuilder()
        sb.appendLine("BEGIN:VCARD")
        sb.appendLine("VERSION:2.1")
        sb.appendLine("N:$name")
        sb.appendLine("TEL:$phone")
        if (emailAddress.isNotEmpty()) sb.appendLine("EMAIL:$emailAddress")
        if (birthday.isNotEmpty()) sb.appendLine("BDAY:$birthday")
        sb.appendLine("END:VCARD")
        return sb.toString()
    }

}