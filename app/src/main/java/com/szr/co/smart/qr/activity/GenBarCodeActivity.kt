package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.adapter.BarCodeTypeAdapter
import com.szr.co.smart.qr.databinding.ActivityGenBarCodeBinding
import com.szr.co.smart.qr.model.BarCodeTypeModel
import com.szr.co.smart.qr.model.QRCodeType
import com.szr.co.smart.qr.utils.dpToPx
import com.szr.co.smart.qr.view.ItemGridDecoration

class GenBarCodeActivity : BaseActivity<ActivityGenBarCodeBinding>() {


    companion object {
        fun toGenType(context: Context, bgId: Int = -1) {
            val intent = Intent(context, GenBarCodeActivity::class.java)
            intent.putExtra("bg_id", bgId)
            context.startActivity(intent)
        }
    }

    private val typeList = arrayListOf<BarCodeTypeModel>(
        BarCodeTypeModel(QRCodeType.BARCODE_ITF, R.string.barcode_itf),
        BarCodeTypeModel(QRCodeType.BARCODE_CODABAR, R.string.barcode_codabar),
        BarCodeTypeModel(QRCodeType.BARCODE_CODE_39, R.string.barcode_code_39),
        BarCodeTypeModel(QRCodeType.BARCODE_CODE_93, R.string.barcode_code_93),
        BarCodeTypeModel(QRCodeType.BARCODE_CODE_128, R.string.barcode_code_128),
        BarCodeTypeModel(QRCodeType.BARCODE_UPC_A, R.string.barcode_upc_a),
        BarCodeTypeModel(QRCodeType.BARCODE_UPC_E, R.string.barcode_upc_e),
        BarCodeTypeModel(QRCodeType.BARCODE_EAN_8, R.string.barcode_ean_8),
        BarCodeTypeModel(QRCodeType.BARCODE_EAN_13, R.string.barcode_ean_13),
    )

    private lateinit var mAdapter: BarCodeTypeAdapter

    override fun inflateBinding(): ActivityGenBarCodeBinding {
        return ActivityGenBarCodeBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.layoutNavTop.ivNavBack.setOnClickListener { onAppBackPage() }
        mBinding.layoutNavTop.tvTitle.setText(R.string.qr_create)

        val bgId = intent.getIntExtra("bg_id", -1)

        mBinding.recycleQrType.layoutManager = GridLayoutManager(this, 2)
        mAdapter = BarCodeTypeAdapter(typeList) {
            BarCodeDataGenActivity.toGenData(this, it, bgId)
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