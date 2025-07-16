package com.szr.co.smart.qr.utils

import android.graphics.Bitmap
import android.util.LruCache

object LruCacheUtils {
    // 最大缓存大小，单位为KB（如10MB）
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8 // 使用可用内存的1/8
    private val lruCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            // 返回Bitmap大小，单位为KB
            return value.byteCount / 1024
        }
    }

    /**
     * 根据字符串生成唯一id（可用hashCode或MD5等）
     */
    fun getIdByString(key: String): String {
        // 简单用hashCode，也可用更复杂算法
        return key.hashCode().toString()
    }

    /**
     * 缓存Bitmap
     */
    fun put(key: String, bitmap: Bitmap) {
        val id = getIdByString(key)
        lruCache.put(id, bitmap)
    }

    /**
     * 缓存Bitmap
     */
    fun putId(id: String, bitmap: Bitmap) {
        lruCache.put(id, bitmap)
    }


    /**
     * 获取缓存Bitmap
     */
    fun get(key: String): Bitmap? {
        val id = getIdByString(key)
        return lruCache.get(id)
    }

    /**
     * 获取缓存Bitmap
     */
    fun getId(id: String): Bitmap? {
        return lruCache.get(id)
    }

    /**
     * 清除缓存
     */
    fun clear() {
        lruCache.evictAll()
    }
} 