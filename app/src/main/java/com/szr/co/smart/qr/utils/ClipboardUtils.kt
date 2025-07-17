package com.szr.co.smart.qr.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtils {
    /**
     * 复制文本到剪切板
     */
    fun copyText(context: Context, text: String, label: String = "text") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
} 