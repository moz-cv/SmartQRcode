package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.lifecycleScope
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.activity.base.BaseAdActivity
import com.szr.co.smart.qr.bill.ViBillHelper
import com.szr.co.smart.qr.bill.position.ViBillPosition
import com.szr.co.smart.qr.databinding.ActivityQrcodeDataGenBinding
import com.szr.co.smart.qr.logic.QrResLogic
import com.szr.co.smart.qr.model.QRCodeType
import com.szr.co.smart.qr.room.AppDB
import com.szr.co.smart.qr.room.model.QRDataModel
import com.szr.co.smart.qr.utils.toast
import com.szr.co.smart.qr.view.qr.BaseQRDataView
import com.szr.co.smart.qr.view.qr.ContactQRView
import com.szr.co.smart.qr.view.qr.SingleEditQRView
import com.szr.co.smart.qr.view.qr.TextQRView
import com.szr.co.smart.qr.view.qr.WebsiteQRView
import com.szr.co.smart.qr.view.qr.WifiQRView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QRCodeDataGenActivity : BaseAdActivity<ActivityQrcodeDataGenBinding>() {


    companion object {
        fun toGenData(context: Context, @QRCodeType type: Int, bgId: Int) {
            val intent = Intent(context, QRCodeDataGenActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("bg_id", bgId)
            context.startActivity(intent)
        }
    }

    private lateinit var mQrDataView: BaseQRDataView
    private var qrCodeType: Int = QRCodeType.QRCODE_WEBSITE
    private var bgId = -1

    override val billHelper: ViBillHelper by lazy {
        ViBillHelper(
            this,
            ViBillPosition.POS_QR_CREATE_CLICK_INTERS,
            mutableListOf(ViBillPosition.POS_QR_RESULT_NATIVE, ViBillPosition.POS_QR_CLICK_SAVE_INTERS),
            ViBillPosition.POS_QR_CREATE_NATIVE,
            mBinding.layoutNativeAd
        )
    }

    override fun inflateBinding(): ActivityQrcodeDataGenBinding {
        return ActivityQrcodeDataGenBinding.inflate(layoutInflater)
    }


    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.layoutNavTop.ivNavBack.setOnClickListener { onAppBackPage() }

        qrCodeType = intent.getIntExtra("type", QRCodeType.QRCODE_WEBSITE)
        bgId = intent.getIntExtra("bg_id", -1)
        val data = QrResLogic.getQRCodeTypeRes(qrCodeType)
        if (data != null) {
            mBinding.layoutNavTop.tvTitle.setText(data.second)
            mBinding.ivQrType.setImageResource(data.first)
        }

        mQrDataView = factoryDataView(qrCodeType)
        mBinding.layoutQrcodeData.addView(
            mQrDataView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
            )
        )

        mBinding.btnGen.setOnClickListener {
            showAdDelayLoad { genCode() }
        }
    }


    private fun factoryDataView(type: Int): BaseQRDataView {
        return when (type) {
            QRCodeType.QRCODE_WEBSITE -> WebsiteQRView(this)
            QRCodeType.QRCODE_CONTACT -> ContactQRView(this)
            QRCodeType.QRCODE_WIFI -> WifiQRView(this)
            QRCodeType.QRCODE_TEXT -> TextQRView(this)
            QRCodeType.QRCODE_PHONE, QRCodeType.QRCODE_X, QRCodeType.QRCODE_FACEBOOK, QRCodeType.QRCODE_INS, QRCodeType.QRCODE_WHATSAPP, QRCodeType.QRCODE_EMAIL -> {
                val qrView = SingleEditQRView(this)
                qrView.setType(type)
                qrView
            }

            else -> TextQRView(this)
        }
    }


    private fun genCode() {
        val data = mQrDataView.qrData()
        if (data == "none") return
        if (data.isEmpty()) {
            toast(getString(R.string.data_is_empty))
            return
        }

        val dataModel = QRDataModel(0, data, qrCodeType, bgId, 1, System.currentTimeMillis())
        GenResultActivity.toResult(this@QRCodeDataGenActivity, dataModel)
        finish()
    }

}