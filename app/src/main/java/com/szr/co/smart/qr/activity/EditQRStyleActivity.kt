package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.adapter.MainQrSrcAdapter
import com.szr.co.smart.qr.databinding.ActivityEditQrstyleBinding
import com.szr.co.smart.qr.logic.QrResLogic
import com.szr.co.smart.qr.room.AppDB
import com.szr.co.smart.qr.room.model.QRDataModel
import com.szr.co.smart.qr.utils.QrUtils
import com.szr.co.smart.qr.utils.dpToPx
import com.szr.co.smart.qr.view.ItemGridDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditQRStyleActivity : BaseActivity<ActivityEditQrstyleBinding>() {

    private lateinit var mAdapter: MainQrSrcAdapter

    companion object {
        fun toStyleResult(context: Context, data: QRDataModel) {
            val intent = Intent(context, EditQRStyleActivity::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }

        fun toStyleResultIntent(context: Context, data: QRDataModel): Intent {
            val intent = Intent(context, EditQRStyleActivity::class.java)
            intent.putExtra("data", data)
            return intent
        }
    }


    override fun inflateBinding(): ActivityEditQrstyleBinding {
        return ActivityEditQrstyleBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        val data = intent.getParcelableExtra<QRDataModel>("data")

        if (data != null) {
            if (QrUtils.isQrCodeType(data.type)) {
                mBinding.ivQr.apply {
                    layoutParams =
                        LinearLayout.LayoutParams(200f.dpToPx().toInt(), 200f.dpToPx().toInt())
                }
            } else {
                mBinding.ivQr.apply {
                    layoutParams =
                        LinearLayout.LayoutParams(200f.dpToPx().toInt(), 100f.dpToPx().toInt())
                }
            }
        }
        genQrCode(data)

        mBinding.ivNavBack.setOnClickListener { onAppBackPage() }
        mBinding.recycleData.layoutManager = GridLayoutManager(this, 3)
        mAdapter = MainQrSrcAdapter(QrResLogic.listBgImages) { id ->

            if (data != null) {
                data.bgId = QrResLogic.listBgImages[id]
                val intent = Intent()
                intent.putExtra("data", data)
                setResult(RESULT_OK, intent)
                genQrCode(data)
            }
        }
        mBinding.recycleData.adapter = mAdapter
        mBinding.recycleData.addItemDecoration(
            ItemGridDecoration(
                3,
                8f.dpToPx().toInt(),
                8f.dpToPx().toInt(),
                false
            )
        )
    }

    private fun genQrCode(data: QRDataModel?) {
        if (data != null) {
            val type = QrUtils.convertTypeZxingType(data.type)
            var bitmap = QrUtils.createQrBitmap(this, data.content, type, data.bgId)
            mBinding.ivQr.setImageBitmap(bitmap)
        }
    }

}