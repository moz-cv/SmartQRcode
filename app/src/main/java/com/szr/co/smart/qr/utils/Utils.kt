package com.szr.co.smart.qr.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Base64
import androidx.core.net.toUri
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object Utils {

    fun openInBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            // 确保新任务中打开（避免在应用内浏览器打开）
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    /**
     * 打开应用设置页面
     */
    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
        }
    }

    fun sameDay(time: Long): Boolean {
        val instant = Instant.ofEpochMilli(time)
        val zonedDateTimeOfGivenTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())

        val currentInstant = Instant.now()
        val zonedDateTimeOfCurrentTime =
            ZonedDateTime.ofInstant(currentInstant, ZoneId.systemDefault())
        return zonedDateTimeOfGivenTime.toLocalDate()
            .equals(zonedDateTimeOfCurrentTime.toLocalDate())
    }


    fun decodeBase64(str: String?): String {
        if (str.isNullOrEmpty()) return ""
        return try {
            String(Base64.decode(str, Base64.NO_WRAP))
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}