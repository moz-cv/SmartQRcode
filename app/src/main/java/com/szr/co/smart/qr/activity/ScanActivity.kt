package com.szr.co.smart.qr.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.ResultPoint
import com.google.zxing.common.HybridBinarizer
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
import java.io.IOException
import java.io.InputStream
import androidx.core.graphics.scale
import androidx.lifecycle.lifecycleScope
import com.szr.co.smart.qr.activity.MainActivity
import com.szr.co.smart.qr.activity.base.BaseAdActivity
import com.szr.co.smart.qr.bill.ViBillHelper
import com.szr.co.smart.qr.bill.position.ViBillPosition
import com.szr.co.smart.qr.utils.Utils
import com.szr.co.smart.qr.utils.permission.PermissionCallback
import com.szr.co.smart.qr.utils.permission.requestCameraPermission
import com.szr.co.smart.qr.utils.permission.requestStoragePermission
import com.szr.co.smart.qr.utils.toast
import kotlinx.coroutines.delay


class ScanActivity : BaseAdActivity<ActivityScanBinding>() {

    private val formats = arrayListOf<BarcodeFormat>(
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

    override val billHelper: ViBillHelper by lazy {
        ViBillHelper(
            this,
            ViBillPosition.POS_QR_SCAN_INTERS,
            mutableListOf(
                ViBillPosition.POS_QR_RESULT_NATIVE,
                ViBillPosition.POS_QR_CLICK_SAVE_INTERS
            ),
            ViBillPosition.POS_QR_RESULT_NATIVE,
            null
        )
    }
    override val showBackAd: Boolean
        get() = true

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

        mBinding.ivActionBack.setOnClickListener { onAppBackPage() }

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                openGallery()
            } else {
                startImageScan()
            }
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
            toResult(result.barcodeFormat, result.text)
        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint?>?) {
        }
    }

    private fun toResult(type: BarcodeFormat, content: String) {
        billHelper.showAd {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = createQRData(type, content)
                ScanResultActivity.toResult(this@ScanActivity, data)
                withContext(Dispatchers.Main) { finish() }

            }
        }

    }

    private fun createQRData(type: BarcodeFormat, content: String): QRDataModel {
        return QRDataModel(
            0,
            content,
            QrUtils.convertType(type, content),
            -1,
            0,
            System.currentTimeMillis()
        )
    }

    private var isTorchOn = false

    private fun setFlashlightIcon(isOn: Boolean) {
        val icon = if (isOn) R.mipmap.ic_flashlight else R.mipmap.ic_flashlight_close
        mBinding.layoutActionFlashlight.findViewById<ImageView>(R.id.iv_flashlight)
            ?.setImageResource(icon)
    }


    private fun openGallery() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_PICK)
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_picture)),
            1990
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1990 && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            if (imageUri == null) {
                toast(getString(R.string.scan_failed))
                return
            }
            scanImage(imageUri)
        }
    }

    private fun scanImage(imageUri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            val startTime = SystemClock.elapsedRealtime()
            var input: InputStream? = null
            val bitmap = try {
                input = contentResolver.openInputStream(imageUri)
                BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                try {
                    input?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            if (bitmap != null) {
                var result: com.google.zxing.Result? = null
                val hints = hashMapOf<DecodeHintType, Any>().apply {
                    put(DecodeHintType.TRY_HARDER, true)
                    put(DecodeHintType.POSSIBLE_FORMATS, formats)
                    put(DecodeHintType.CHARACTER_SET, "UTF-8")
                }

                try {
                    val maxDimension = 800
                    val scale = minOf(
                        maxDimension.toFloat() / bitmap.width,
                        maxDimension.toFloat() / bitmap.height,
                        1f
                    )
                    val scaledWidth = (bitmap.width * scale).toInt()
                    val scaledHeight = (bitmap.height * scale).toInt()

                    val bitmap = bitmap.scale(scaledWidth, scaledHeight)

                    val intArray = IntArray(bitmap.width * bitmap.height)
                    bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

                    val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
                    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                    result = MultiFormatReader().decode(binaryBitmap, hints)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    bitmap.recycle()
                }

                val timeElapsed = SystemClock.elapsedRealtime() - startTime
                if (timeElapsed < 150) {
                    delay(150 - timeElapsed)
                }

                withContext(Dispatchers.Main) {
                    if (result == null) {
                        toast(getString(R.string.scan_failed))
                    } else {
                        toResult(result.barcodeFormat, result.text)
                    }
                }
            }
        }
    }

    /**
     * 申请相机权限
     */
    private fun startImageScan() {
        requestStoragePermission(object : PermissionCallback {
            override fun onPermissionGranted(permissions: Array<String>) {
                openGallery()
            }

            override fun onPermissionDenied(deniedPermissions: Array<String>) {
            }

            override fun onPermissionPermanentlyDenied(permanentlyDeniedPermissions: Array<String>) {
                showPermissionPermanentlyDeniedDialog(getString(R.string.set_open_storage_hint))
            }
        })
    }

    /**
     * 显示权限被永久拒绝的对话框
     */
    private fun showPermissionPermanentlyDeniedDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_denied))
            .setMessage(message)
            .setPositiveButton(getString(R.string.go_set)) { _, _ ->
                // 跳转到应用设置页面
                Utils.openAppSettings(this)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}