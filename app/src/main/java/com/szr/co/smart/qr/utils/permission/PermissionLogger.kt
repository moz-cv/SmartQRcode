package com.szr.co.smart.qr.utils.permission

import android.util.Log

/**
 * 权限框架日志工具类
 * 统一管理Debug模式下的日志输出
 */
object PermissionLogger {
    
    private const val TAG_PREFIX = "PermissionKit"
    private const val DEBUG = false
    
    /**
     * 输出Debug日志
     */
    fun d(tag: String, message: String) {
        if (DEBUG) {
            Log.d("$TAG_PREFIX-$tag", message)
        }
    }
    
    /**
     * 输出Warning日志
     */
    fun w(tag: String, message: String) {
        if (DEBUG) {
            Log.w("$TAG_PREFIX-$tag", message)
        }
    }
    
    /**
     * 输出Error日志
     */
    fun e(tag: String, message: String) {
        if (DEBUG) {
            Log.e("$TAG_PREFIX-$tag", message)
        }
    }
    
    /**
     * 输出Error日志（带异常）
     */
    fun e(tag: String, message: String, throwable: Throwable) {
        if (DEBUG) {
            Log.e("$TAG_PREFIX-$tag", message, throwable)
        }
    }
    
    /**
     * 输出Info日志
     */
    fun i(tag: String, message: String) {
        if (DEBUG) {
            Log.i("$TAG_PREFIX-$tag", message)
        }
    }
    
    /**
     * 输出Verbose日志
     */
    fun v(tag: String, message: String) {
        if (DEBUG) {
            Log.v("$TAG_PREFIX-$tag", message)
        }
    }
    
    /**
     * 检查是否为Debug模式
     */
    fun isDebug(): Boolean {
        return DEBUG
    }
} 