package com.szr.co.smart.qr.activity

import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.databinding.ActivityLanguageBinding

class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {

    override fun inflateBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.ivNavBack.setOnClickListener { onAppBackPage() }
    }
}