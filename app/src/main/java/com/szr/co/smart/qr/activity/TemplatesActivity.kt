package com.szr.co.smart.qr.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.adapter.MainQrSrcAdapter
import com.szr.co.smart.qr.databinding.ActivityTemplatesBinding
import com.szr.co.smart.qr.dialog.CreateTypeDialog
import com.szr.co.smart.qr.logic.QrResLogic
import com.szr.co.smart.qr.utils.dpToPx
import com.szr.co.smart.qr.view.ItemGridDecoration

class TemplatesActivity : BaseActivity<ActivityTemplatesBinding>() {

    private lateinit var mAdapter: MainQrSrcAdapter

    override fun inflateBinding(): ActivityTemplatesBinding {
        return ActivityTemplatesBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.layoutNavTop.ivNavBack.setOnClickListener { onAppBackPage() }
        mBinding.layoutNavTop.tvTitle.text = getString(R.string.popular_templates)
        mBinding.recycleData.layoutManager = GridLayoutManager(this, 3)
        mAdapter = MainQrSrcAdapter(QrResLogic.listBgImages) { id ->
            CreateTypeDialog(this) {
                if (it == 0) {
                    GenQRCodeActivity.toGenType(this, id)
                } else {
                    GenBarCodeActivity.toGenType(this, id)
                }
                finish()
            }.show()
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
}