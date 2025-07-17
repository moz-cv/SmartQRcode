package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.adapter.QRCodeTypeAdapter
import com.szr.co.smart.qr.databinding.ActivityGenQrcodeBinding
import com.szr.co.smart.qr.model.QRCodeType
import com.szr.co.smart.qr.model.QRCodeTypeModel
import com.szr.co.smart.qr.utils.dpToPx
import com.szr.co.smart.qr.view.ItemGridDecoration

class GenQRCodeActivity : BaseActivity<ActivityGenQrcodeBinding>() {
    companion object {
        fun toGenType(context: Context, bgId: Int =-1) {
            val intent = Intent(context, GenQRCodeActivity::class.java)
            intent.putExtra("bg_id", bgId)
            context.startActivity(intent)
        }
    }

    private val typeList = arrayListOf<QRCodeTypeModel>(
        QRCodeTypeModel(QRCodeType.QRCODE_WEBSITE, R.string.website, R.mipmap.ic_qr_website),
        QRCodeTypeModel(QRCodeType.QRCODE_EMAIL, R.string.email, R.mipmap.ic_qr_email),
        QRCodeTypeModel(QRCodeType.QRCODE_CONTACT, R.string.contact, R.mipmap.ic_qr_contact),
        QRCodeTypeModel(QRCodeType.QRCODE_PHONE, R.string.phone, R.mipmap.ic_qr_phone),
        QRCodeTypeModel(QRCodeType.QRCODE_TEXT, R.string.text, R.mipmap.ic_qr_text),
        QRCodeTypeModel(QRCodeType.QRCODE_WIFI, R.string.wifi, R.mipmap.ic_qr_wifi),
        QRCodeTypeModel(QRCodeType.QRCODE_X, R.string.x, R.mipmap.ic_qr_x),
        QRCodeTypeModel(QRCodeType.QRCODE_FACEBOOK, R.string.facebook, R.mipmap.ic_qr_facebook),
        QRCodeTypeModel(QRCodeType.QRCODE_INS, R.string.ins, R.mipmap.ic_qr_ins),
        QRCodeTypeModel(QRCodeType.QRCODE_WHATSAPP, R.string.whatsapp, R.mipmap.ic_qr_wapp),
    )

    private lateinit var mAdapter: QRCodeTypeAdapter

    override fun inflateBinding(): ActivityGenQrcodeBinding {
        return ActivityGenQrcodeBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.layoutNavTop.ivNavBack.setOnClickListener { onAppBackPage() }
        mBinding.layoutNavTop.tvTitle.setText(R.string.qr_create)
        val bgId = intent.getIntExtra("bg_id", -1)

        mBinding.recycleQrType.layoutManager = GridLayoutManager(this, 2)
        mAdapter = QRCodeTypeAdapter(typeList) {
            QRCodeDataGenActivity.toGenData(this, it,bgId)
        }
        mBinding.recycleQrType.adapter = mAdapter
        mBinding.recycleQrType.addItemDecoration(
            ItemGridDecoration(
                2,
                12f.dpToPx().toInt(),
                12f.dpToPx().toInt(),
                false
            )
        )
    }
}