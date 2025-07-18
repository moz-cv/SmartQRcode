package com.szr.co.smart.qr.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.encoder.QRCode
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.databinding.LayoutPushCodeBinding
import com.szr.co.smart.qr.utils.QrUtils
import com.szr.co.smart.qr.utils.dpToPx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PushCodeDialog(context: Context, val url: String,val callback:()-> Unit) : Dialog(context, R.style.dialog) {


    private lateinit var mBinding: LayoutPushCodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = LayoutPushCodeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val layoutParams = window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        window?.attributes = layoutParams

        setCanceledOnTouchOutside(false)

        SmartApp.instance.scope.launch {
            val bitmap = QrUtils.generateQRCode(
                url,
                BarcodeFormat.QR_CODE,
                200f.dpToPx().toInt(),
                200f.dpToPx().toInt()
            )
            withContext(Dispatchers.Main) {
                mBinding.qr.setImageBitmap(bitmap)
            }
        }


        mBinding.layoutClose.setOnClickListener {
            dismiss()
        }

        mBinding.tvAction.setOnClickListener {
            callback()
            dismiss()
        }
    }
}