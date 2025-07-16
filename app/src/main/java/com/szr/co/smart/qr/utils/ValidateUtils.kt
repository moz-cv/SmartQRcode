package com.szr.co.smart.qr.utils

import android.util.Patterns

object ValidateUtils {
    /**
     * 国际手机号校验（宽松，支持+号、空格、-、括号等，最少7位数字）
     */
    fun isValidPhone(phone: String): Boolean {
        val regex = "^\\+?[0-9. ()-]{7,}$".toRegex()
        return phone.matches(regex)
    }

    /**
     * 邮箱校验
     */
    fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(regex)
    }

    /**
     * 网址校验，支持http(s)://或www.开头
     */
    fun isValidWebsite(url: String): Boolean {
        val lower = url.lowercase()
        return (lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("www."))
                && Patterns.WEB_URL.matcher(url).matches()
    }
} 