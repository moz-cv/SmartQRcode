package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import com.google.zxing.BarcodeFormat
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.databinding.ActivityGenResultBinding
import com.szr.co.smart.qr.room.model.QRDataModel
import com.szr.co.smart.qr.utils.QrUtils

class GenResultActivity : BaseActivity<ActivityGenResultBinding>() {

    companion object {
        fun toResult(context: Context, data: QRDataModel) {
            val intent = Intent(context, GenResultActivity::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }
    }

    override fun inflateBinding(): ActivityGenResultBinding {
        return ActivityGenResultBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        val data = intent.getParcelableExtra<QRDataModel>("data")
        if (data != null) {
            val type = QrUtils.convertTypeZxingType(data.type)
            if (type == BarcodeFormat.QR_CODE) {
                val bitmap = QrUtils.generateQRCode(data.content, type)
                mBinding.ivQrcode.setImageBitmap(bitmap)
            } else {
                val bitmap = QrUtils.generateBarCode(data.content, type)
                mBinding.ivQrcode.setImageBitmap(bitmap)
            }

            mBinding.tvContent.text = data.content
        }
    }
}