package com.szr.co.smart.qr.activity

import android.content.Intent
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.databinding.ActivityStartBinding

class StartActivity : BaseActivity<ActivityStartBinding>() {

    override fun inflateBinding(): ActivityStartBinding {
        return ActivityStartBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        startActivity(Intent(this, MainActivity::class.java))
    }
}