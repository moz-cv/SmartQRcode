package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.GenResultActivity
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.activity.base.BaseAdActivity
import com.szr.co.smart.qr.bill.ViBillHelper
import com.szr.co.smart.qr.bill.position.ViBillPosition
import com.szr.co.smart.qr.databinding.ActivityScanResultBinding
import com.szr.co.smart.qr.room.AppDB
import com.szr.co.smart.qr.room.model.QRDataModel
import com.szr.co.smart.qr.utils.ClipboardUtils
import com.szr.co.smart.qr.utils.QrUtils
import com.szr.co.smart.qr.utils.ShareUtils
import com.szr.co.smart.qr.utils.Utils
import com.szr.co.smart.qr.utils.ValidateUtils
import com.szr.co.smart.qr.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ScanResultActivity : BaseAdActivity<ActivityScanResultBinding>() {


    companion object {
        fun toResult(context: Context, data: QRDataModel) {
            val intent = Intent(context, ScanResultActivity::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }
    }

    override val billHelper: ViBillHelper by lazy {
        ViBillHelper(
            this,
            ViBillPosition.POS_QR_CLICK_SAVE_INTERS,
            mutableListOf(ViBillPosition.POS_QR_RESULT_NATIVE, ViBillPosition.POS_QR_CLICK_SAVE_INTERS),
            ViBillPosition.POS_QR_RESULT_NATIVE,
            mBinding.layoutNativeAd
        )
    }

    private var mData: QRDataModel? = null
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data?.getParcelableExtra<QRDataModel>("data")
                if (data != null) {
                    mData = data
                    fillResultQrInfo()
                }
            }
        }

    override fun inflateBinding(): ActivityScanResultBinding {
        return ActivityScanResultBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.ivNavBack.setOnClickListener { onAppBackPage() }
        val data = intent.getParcelableExtra<QRDataModel>("data")
        mData = data
        fillResultQrInfo()

        mBinding.layoutSave.setOnClickListener {

            billHelper.showAd {
                //保存数据到数据库
                lifecycleScope.launch(Dispatchers.IO) {
                    if (mData != null) AppDB.db.qrDataDao().insert(mData!!)
                    withContext(Dispatchers.Main) { toast(getString(R.string.saved_successfully)) }
                }
            }
        }

        mBinding.layoutStyle.setOnClickListener {
            if (mData != null) {
                activityResultLauncher.launch(
                    EditQRStyleActivity.toStyleResultIntent(this, mData!!)
                )
            }
        }

        mBinding.layoutOpen.setOnClickListener {
            if (mData == null) return@setOnClickListener
            if (ValidateUtils.isValidWebsite(mData!!.content)) {
                Utils.openInBrowser(this, mData!!.content)
            } else {
                val url = "https://www.google.com/search?q=${mData!!.content}"
                Utils.openInBrowser(this, url)
            }
        }

        mBinding.ivActionShare.setOnClickListener {
            if (mData == null) return@setOnClickListener
            ShareUtils.shareText(
                this, mData!!.content,
                getString(R.string.share)
            )
        }

        mBinding.ivActionCopy.setOnClickListener {
            if (mData == null) return@setOnClickListener
            ClipboardUtils.copyText(this, mData!!.content)
            toast(getString(R.string.copied))
        }
    }

    private fun fillResultQrInfo() {
        val bitmap = createQrBitmap()
        if (bitmap == null) return
        mBinding.ivQrcode.setImageBitmap(bitmap)
        mBinding.tvContent.text = mData?.content
    }

    private fun createQrBitmap(): Bitmap? {
        if (mData == null) return null
        val type = QrUtils.convertTypeZxingType(mData!!.type)
        return QrUtils.createQrBitmap(this, mData!!.content, type, mData!!.bgId)
    }
}