package com.szr.co.smart.qr.dialog

import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.HistoryCodeActivity
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.databinding.LayoutHistoryMenuBinding
import com.szr.co.smart.qr.databinding.LayoutQrCreateBinding


class CreateTypeDialog(val activity: BaseActivity<*>,val callback:(Int)-> Unit) :
    BottomSheetDialog(activity, R.style.BottomSheetDialog) {

    private lateinit var mBinding: LayoutQrCreateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = LayoutQrCreateBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setCanceledOnTouchOutside(true)
        val layoutParams = window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        mBinding.layoutCreateBarcode.setOnClickListener {
            callback(1)
            dismiss()
        }

        mBinding.layoutCreateQrcode.setOnClickListener {
            callback(0)
            dismiss()
        }

    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}