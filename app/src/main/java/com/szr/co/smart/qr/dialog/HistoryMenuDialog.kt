package com.szr.co.smart.qr.dialog

import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.HistoryCodeActivity
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.databinding.LayoutHistoryMenuBinding


class HistoryMenuDialog(val activity: BaseActivity<*>) :
    BottomSheetDialog(activity, R.style.BottomSheetDialog) {

    private lateinit var mBinding: LayoutHistoryMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = LayoutHistoryMenuBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setCanceledOnTouchOutside(true)
        val layoutParams = window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams



        mBinding.layoutGen.setOnClickListener {
            HistoryCodeActivity.toHistory(activity, HistoryCodeActivity.Type.GEN)
            dismiss()
        }

        mBinding.layoutScan.setOnClickListener {
            HistoryCodeActivity.toHistory(activity, HistoryCodeActivity.Type.SCAN)
            dismiss()
        }

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}