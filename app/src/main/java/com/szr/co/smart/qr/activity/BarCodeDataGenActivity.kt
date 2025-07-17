package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.databinding.ActivityBarCodeDataGenBinding
import com.szr.co.smart.qr.logic.QrResLogic
import com.szr.co.smart.qr.model.QRCodeType
import com.szr.co.smart.qr.room.AppDB
import com.szr.co.smart.qr.room.model.QRDataModel
import com.szr.co.smart.qr.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BarCodeDataGenActivity : BaseActivity<ActivityBarCodeDataGenBinding>() {
    companion object {
        fun toGenData(context: Context, @QRCodeType type: Int, bgId: Int) {
            val intent = Intent(context, BarCodeDataGenActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("bg_id", bgId)
            context.startActivity(intent)
        }
    }

    private var qrCodeType: Int = QRCodeType.QRCODE_WEBSITE
    private var bgId = -1

    override fun inflateBinding(): ActivityBarCodeDataGenBinding {
        return ActivityBarCodeDataGenBinding.inflate(layoutInflater)
    }

    override fun lightStatusBar(): Boolean {
        return false
    }

    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.layoutNavTop.ivNavBack.setOnClickListener { onAppBackPage() }
        qrCodeType = intent.getIntExtra("type", QRCodeType.QRCODE_WEBSITE)
        bgId = intent.getIntExtra("bg_id", -1)
        val data = QrResLogic.getQRCodeTypeResName(qrCodeType)
        if (data != null) {
            mBinding.layoutNavTop.tvTitle.setText(data)
        }
        mBinding.barCodeDataView.setType(qrCodeType)

        mBinding.buttonCreate.setOnClickListener {
            genCode()
        }
    }


    private fun genCode() {
        val data = mBinding.barCodeDataView.qrData()
        if (data == "none") return
        if (data.isEmpty()) {
            toast(getString(R.string.data_is_empty))
            return
        }
        val dataModel = QRDataModel(0, data, qrCodeType, bgId, 1, System.currentTimeMillis())
        GenResultActivity.toResult(this@BarCodeDataGenActivity, dataModel)
        finish()
    }
}