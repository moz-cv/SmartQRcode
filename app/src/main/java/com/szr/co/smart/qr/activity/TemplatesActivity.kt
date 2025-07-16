package com.szr.co.smart.qr.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.adapter.MainQrSrcAdapter
import com.szr.co.smart.qr.databinding.ActivityTemplatesBinding
import com.szr.co.smart.qr.utils.dpToPx
import com.szr.co.smart.qr.view.ItemGridDecoration

class TemplatesActivity : BaseActivity<ActivityTemplatesBinding>() {

    private val listImages = arrayListOf<Int>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
    private lateinit var mAdapter: MainQrSrcAdapter

    override fun inflateBinding(): ActivityTemplatesBinding {
        return ActivityTemplatesBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.recycleData.layoutManager = GridLayoutManager(this, 3)
        mAdapter = MainQrSrcAdapter(listImages){

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