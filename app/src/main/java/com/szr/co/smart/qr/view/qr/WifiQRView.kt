package com.szr.co.smart.qr.view.qr

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.LayoutQrcodeWebsiteBinding
import com.szr.co.smart.qr.databinding.LayoutQrcodeWifiBinding
import com.szr.co.smart.qr.utils.toast

class WifiQRView : BaseQRDataView {

    private var mBinding: LayoutQrcodeWifiBinding
    private var pwdShow = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        mBinding = LayoutQrcodeWifiBinding.inflate(LayoutInflater.from(context), this, true)

        mBinding.ivEye.setOnClickListener {
            pwdShow = !pwdShow
            mBinding.etWifiPassword.transformationMethod =
                if (pwdShow) HideReturnsTransformationMethod.getInstance() else  PasswordTransformationMethod.getInstance()

            mBinding.ivEye.setImageResource(if (pwdShow) R.mipmap.ic_eye_open else R.mipmap.ic_eye_close)
        }
    }


    override fun qrData(): String {
        val ssid = mBinding.etWifiName.text.toString().trim()
        val password = mBinding.etWifiPassword.text.toString().trim()
        if (ssid.isEmpty()) {
            context.toast(context.getString(R.string.wifi_invalidate))
            return "none"
        }
        val type = if (password.isEmpty()) "nopass" else "WPA"
        val sb = StringBuilder()
        sb.append("WIFI:T:").append(type).append(";")
        sb.append("S:").append(ssid).append(";")
        if (password.isNotEmpty()) {
            sb.append("P:").append(password).append(";")
        }
        sb.append(";")
        return sb.toString()
    }

}