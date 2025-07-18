package com.szr.co.smart.qr.event

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.szr.co.smart.qr.BuildConfig

object AppEvent {

    fun event(event: String) {
        if (BuildConfig.DEBUG) Log.d("FirebaseEvent", "event: $event")
        Firebase.analytics.logEvent(event, null)
    }

    fun event(event: String, params: Bundle? = null) {
        if (BuildConfig.DEBUG) Log.d("FirebaseEvent", "event: $event params: $params")
        Firebase.analytics.logEvent(event, params)
    }

    fun eventValue(event: String, value: String) {
        if (BuildConfig.DEBUG) Log.d("FirebaseEvent", "event: $event params: [gs:${value}]")
        Firebase.analytics.logEvent(event, Bundle().apply {
            putString("smart", value)
        })
    }
}