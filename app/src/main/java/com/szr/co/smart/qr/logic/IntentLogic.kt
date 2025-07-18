package com.szr.co.smart.qr.logic

import android.content.Intent
import com.szr.co.smart.qr.event.AppEvent
import java.net.URL
import kotlin.let
import kotlin.text.isNullOrEmpty


class IntentLogic {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { IntentLogic() }
    }

    private var url: String? = null

    fun parseIntentData(intent: Intent) {
        val action = intent.action
        if (action == "VIDEO_PUSH") {
            url = intent.getStringExtra("url")
            AppEvent.event("sq_noti_cli")
        }
    }

    fun getVideoUrl(): String? {
        val result = url
        url = null
        return result
    }
}