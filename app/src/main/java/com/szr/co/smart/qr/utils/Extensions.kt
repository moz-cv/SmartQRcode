package com.szr.co.smart.qr.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Base64
import android.util.TypedValue
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.net.URL
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


fun String?.decodeBase64(): String? {
    if (this.isNullOrEmpty()) return null
    return try {
        String(Base64.decode(this, Base64.NO_WRAP))
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Number.dpToPx(): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(),
    Resources.getSystem().displayMetrics
)

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}