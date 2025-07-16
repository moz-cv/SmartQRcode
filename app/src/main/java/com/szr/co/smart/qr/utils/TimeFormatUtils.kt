package com.szr.co.smart.qr.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeFormatUtils {
    private val formatter = SimpleDateFormat("yyyy/MM/dd · H:mm", Locale.getDefault())

    /**
     * 将时间戳（毫秒）格式化为 2025/04/16 · 8:20 这种格式
     */
    fun formatDateTime(millis: Long): String {
        return formatter.format(Date(millis))
    }

    /**
     * 将Date对象格式化为 2025/04/16 · 8:20 这种格式
     */
    fun formatDateTime(date: Date): String {
        return formatter.format(date)
    }
} 