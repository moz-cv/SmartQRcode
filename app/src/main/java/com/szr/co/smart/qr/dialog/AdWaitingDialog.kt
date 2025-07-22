package com.szr.co.smart.qr.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.LayoutAdWaitingBinding
import com.szr.co.smart.qr.databinding.LayoutPushCodeBinding
import com.szr.co.smart.qr.databinding.LayoutWaitingBinding
import com.szr.co.smart.qr.utils.dpToPx

class AdWaitingDialog(context: Context) : Dialog(context, R.style.dialog) {

    private lateinit var mBinding: LayoutAdWaitingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = LayoutAdWaitingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val layoutParams = window?.attributes
        layoutParams?.width = 220f.dpToPx().toInt()
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        setCanceledOnTouchOutside(false)


    }
}