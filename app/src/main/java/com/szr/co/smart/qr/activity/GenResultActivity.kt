package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.util.Util
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.databinding.ActivityGenResultBinding
import com.szr.co.smart.qr.room.AppDB
import com.szr.co.smart.qr.room.model.QRDataModel
import com.szr.co.smart.qr.utils.QrUtils
import com.szr.co.smart.qr.utils.ShareUtils
import com.szr.co.smart.qr.utils.Utils
import com.szr.co.smart.qr.utils.ValidateUtils
import com.szr.co.smart.qr.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GenResultActivity : BaseActivity<ActivityGenResultBinding>() {


    companion object {
        fun toResult(context: Context, data: QRDataModel) {
            val intent = Intent(context, GenResultActivity::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }
    }

    private var mData: QRDataModel? = null

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data?.getParcelableExtra<QRDataModel>("data")
                if (data != null) {
                    mData = data
                    genResultQr()
                }
            }
        }


    override fun inflateBinding(): ActivityGenResultBinding {
        return ActivityGenResultBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.ivNavBack.setOnClickListener { onAppBackPage() }
        val data = intent.getParcelableExtra<QRDataModel>("data")
        mData = data
        genResultQr()

        mBinding.layoutSave.setOnClickListener {
            //保存数据到数据库
            lifecycleScope.launch(Dispatchers.IO) {
                if (mData != null) AppDB.db.qrDataDao().insert(mData!!)
                withContext(Dispatchers.Main) { toast(getString(R.string.saved_successfully)) }
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
                val url = "https://www.google.com.hk/search?q=${mData!!.content}"
                Utils.openInBrowser(this, url)
            }
        }

        mBinding.ivActionShare.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val bitmap = createQrBitmap()
                if (bitmap == null) return@launch
                withContext(Dispatchers.Main) {
                    ShareUtils.shareBitmap(
                        this@GenResultActivity,
                        bitmap,
                        getString(R.string.share)
                    )
                }
            }
        }
    }

    private fun genResultQr() {
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