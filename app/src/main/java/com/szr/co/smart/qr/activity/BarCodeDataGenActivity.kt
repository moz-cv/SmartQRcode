package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.QRCodeDataGenActivity
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.databinding.ActivityBarCodeDataGenBinding
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

class BarCodeDataGenActivity : BaseActivity<ActivityBarCodeDataGenBinding>() {
    companion object {
        fun toGenData(context: Context, @QRCodeType type: Int) {
            val intent = Intent(context, BarCodeDataGenActivity::class.java)
            intent.putExtra("type", type)
            context.startActivity(intent)
        }
    }

    private var qrCodeType: Int = QRCodeType.QRCODE_WEBSITE

    override fun inflateBinding(): ActivityBarCodeDataGenBinding {
        return ActivityBarCodeDataGenBinding.inflate(layoutInflater)
    }


    override fun initOnCreate() {
        super.initOnCreate()
        qrCodeType = intent.getIntExtra("type", QRCodeType.QRCODE_WEBSITE)

        val data = QrResLogic.getQRCodeTypeRes(qrCodeType)
        if (data != null) {
            mBinding.layoutNavTop.tvTitle.setText(data.second)
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
        lifecycleScope.launch(Dispatchers.IO) {
            val dataModel = QRDataModel(0, data, qrCodeType, 1, System.currentTimeMillis())
            AppDB.db.qrDataDao().insert(dataModel)
            withContext(Dispatchers.Main) {
                GenResultActivity.toResult(this@BarCodeDataGenActivity, dataModel)
                finish()
            }
        }
    }
}