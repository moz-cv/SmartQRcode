package com.szr.co.smart.qr.utils.permission

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.szr.co.smart.qr.BuildConfig

/**
 * 权限管理器
 * 提供便捷的权限申请方法
 */
class PermissionManager private constructor() {

    companion object {
        private const val TAG = "Manager"
        private const val FRAGMENT_TAG_PREFIX = "permission_fragment_kit"
        private  var DEBUG = BuildConfig.DEBUG

        @Volatile
        private var INSTANCE: PermissionManager? = null

        fun getInstance(): PermissionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PermissionManager().also { INSTANCE = it }
            }
        }

        private fun logD(message: String) {
            if (DEBUG) {
                Log.d(TAG, message)
            }
        }

        private fun logW(message: String) {
            if (DEBUG) {
                Log.w(TAG, message)
            }
        }

        private fun logE(message: String) {
            if (DEBUG) {
                Log.e(TAG, message)
            }
        }
    }

    /**
     * 申请权限
     * @param activity FragmentActivity实例
     * @param permissions 需要申请的权限列表
     * @param callback 权限申请回调
     */
    fun requestPermissions(
        activity: FragmentActivity,
        permissions: Array<String>,
        callback: PermissionCallback
    ) {
        PermissionLogger.d(TAG, "Requesting permissions: ${permissions.contentToString()}")

        // 检查是否已经有权限
        if (areAllPermissionsGranted(activity, permissions)) {
            PermissionLogger.d(TAG, "All permissions already granted")
            callback.onPermissionGranted(permissions)
            return
        }

        // 添加到Activity
        val fragmentManager = activity.supportFragmentManager
        // 移除可能存在的旧Fragment
        val existingFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_PREFIX)
        val invisibleFragment = if (existingFragment != null) {
            existingFragment as PermissionFragment
        } else {
            val fragment = PermissionFragment.newInstance()
            fragment.setPermissionCallback(callback)
            fragmentManager.beginTransaction()
                .add(fragment, FRAGMENT_TAG_PREFIX)
                .commitNowAllowingStateLoss()
            fragment
        }
        // 开始申请权限
        invisibleFragment.requestPermissions(permissions)
    }

    /**
     * 检查权限是否已授予
     * @param context Context实例
     * @param permission 权限名称
     * @return 是否已授予
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 检查所有权限是否已授予
     * @param context Context实例
     * @param permissions 权限列表
     * @return 是否所有权限都已授予
     */
    fun areAllPermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(context, it) }
    }

    /**
     * 检查是否有权限被拒绝
     * @param context Context实例
     * @param permissions 权限列表
     * @return 是否有权限被拒绝
     */
    fun hasDeniedPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.any { !isPermissionGranted(context, it) }
    }

    /**
     * 获取被拒绝的权限列表
     * @param context Context实例
     * @param permissions 权限列表
     * @return 被拒绝的权限列表
     */
    fun getDeniedPermissions(context: Context, permissions: Array<String>): Array<String> {
        return permissions.filter { !isPermissionGranted(context, it) }.toTypedArray()
    }

    /**
     * 清理权限Fragment
     * @param activity FragmentActivity实例
     */
    fun cleanupFragment(activity: FragmentActivity) {
        val fragment = activity.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG_PREFIX)
        if (fragment != null) {
            activity.supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
    }
} 