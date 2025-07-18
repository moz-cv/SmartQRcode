package com.szr.co.smart.qr.dialog

import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.databinding.LayoutAlertBottomBinding
import com.szr.co.smart.qr.databinding.LayoutQrCreateBinding


class PostBottomDialog(val activity: BaseActivity<*>, val allowAction: () -> Unit,
                       val dismissAction: (Boolean) -> Unit) :
    BottomSheetDialog(activity, R.style.BottomSheetDialog) {

    private lateinit var mBinding: LayoutAlertBottomBinding
    private var canHandleNext = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = LayoutAlertBottomBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setCanceledOnTouchOutside(false)
        val layoutParams = window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        setOnDismissListener {
            dismissAction(canHandleNext)
        }


        mBinding.ivClose.setOnClickListener {
            dismiss()
        }

        mBinding.buttonAllow.setOnClickListener {
            dismiss()
            canHandleNext = false
            allowAction()
        }

    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}