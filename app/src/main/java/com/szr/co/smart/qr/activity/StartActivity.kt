package com.szr.co.smart.qr.activity

import android.content.Intent
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.databinding.ActivityStartBinding
import com.szr.co.smart.qr.logic.IntentLogic

class StartActivity : BaseActivity<ActivityStartBinding>() {

    override fun inflateBinding(): ActivityStartBinding {
        return ActivityStartBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        IntentLogic.instance.parseIntentData(intent)
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        IntentLogic.instance.parseIntentData(intent)
    }
}