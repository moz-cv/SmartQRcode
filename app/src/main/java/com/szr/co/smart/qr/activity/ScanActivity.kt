package com.szr.co.smart.qr.activity

import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.databinding.ActivityScanBinding
import com.szr.co.smart.qr.room.AppDB
import com.szr.co.smart.qr.room.model.QRDataModel
import com.szr.co.smart.qr.utils.QrUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ScanActivity : BaseActivity<ActivityScanBinding>() {

    override fun inflateBinding(): ActivityScanBinding {
        return ActivityScanBinding.inflate(layoutInflater)
    }

    override fun systemBarPaddingTop(): Int {
        return 0
    }

    override fun lightStatusBar(): Boolean {
        return true
    }


    override fun initOnCreate() {
        super.initOnCreate()
        val formats = arrayListOf<BarcodeFormat>(
            BarcodeFormat.CODABAR,
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93,
            BarcodeFormat.CODE_128,
            BarcodeFormat.EAN_8,
            BarcodeFormat.EAN_13,
            BarcodeFormat.ITF,
            BarcodeFormat.UPC_A,
            BarcodeFormat.UPC_E,
            BarcodeFormat.QR_CODE
        )
        mBinding.zxingBarcodeSurface.setDecoderFactory(DefaultDecoderFactory(formats))
        mBinding.zxingBarcodeSurface.decodeSingle(callback)

        // 初始化闪光灯图标状态（防止状态不同步）
        setFlashlightIcon(isTorchOn)

        mBinding.layoutActionFlashlight.setOnClickListener {
            isTorchOn = !isTorchOn
            mBinding.zxingBarcodeSurface.setTorch(isTorchOn)
            setFlashlightIcon(isTorchOn)
        }

        mBinding.layoutActionImage.setOnClickListener {

        }
    }


    override fun onResume() {
        super.onResume()
        mBinding.zxingBarcodeSurface.resume()
    }

    override fun onPause() {
        super.onPause()
        mBinding.zxingBarcodeSurface.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            toResult(result)
        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint?>?) {
        }
    }

    private fun toResult(result: BarcodeResult) {
        lifecycleScope.launch(Dispatchers.IO) {
            val data = createQRData(result)
            AppDB.db.qrDataDao().insert(data)
            ScanResultActivity.toResult(this@ScanActivity, data)
            withContext(Dispatchers.Main) { finish() }
        }
    }


    private fun createQRData(result: BarcodeResult): QRDataModel {

        return QRDataModel(
            0,
            result.text,
            QrUtils.convertType(result.barcodeFormat, result.text),
            0,
            System.currentTimeMillis()
        )
    }

    private var isTorchOn = false

    private fun setFlashlightIcon(isOn: Boolean) {
        val icon = if (isOn) R.mipmap.ic_flashlight else R.mipmap.ic_flashlight_close
        mBinding.layoutActionFlashlight.findViewById<ImageView>(R.id.iv_flashlight)?.setImageResource(icon)
    }
}