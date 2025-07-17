package com.szr.co.smart.qr.utils.permission

import android.Manifest
import androidx.fragment.app.FragmentActivity

/**
 * FragmentActivity的权限申请扩展函数
 */

/**
 * 申请单个权限
 * @param permission 权限名称
 * @param callback 权限申请回调
 */
fun FragmentActivity.requestPermission(
    permission: String,
    callback: PermissionCallback
) {
    PermissionManager.getInstance().requestPermissions(
        this,
        arrayOf(permission),
        callback
    )
}

/**
 * 申请多个权限
 * @param permissions 权限名称数组
 * @param callback 权限申请回调
 */
fun FragmentActivity.requestPermissions(
    permissions: Array<String>,
    callback: PermissionCallback
) {
    PermissionManager.getInstance().requestPermissions(
        this,
        permissions,
        callback
    )
}

/**
 * 申请相机权限
 * @param callback 权限申请回调
 */
fun FragmentActivity.requestCameraPermission(callback: PermissionCallback) {
    requestPermission(Manifest.permission.CAMERA, callback)
}

/**
 * 申请存储权限
 * @param callback 权限申请回调
 */
fun FragmentActivity.requestStoragePermission(callback: PermissionCallback) {
    requestPermissions(arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    ), callback)
}

/**
 * 申请位置权限
 * @param callback 权限申请回调
 */
fun FragmentActivity.requestLocationPermission(callback: PermissionCallback) {
    requestPermissions(arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ), callback)
}

/**
 * 申请相机和存储权限
 * @param callback 权限申请回调
 */
fun FragmentActivity.requestCameraAndStoragePermission(callback: PermissionCallback) {
    requestPermissions(arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ), callback)
}

/**
 * 申请麦克风权限
 * @param callback 权限申请回调
 */
fun FragmentActivity.requestMicrophonePermission(callback: PermissionCallback) {
    requestPermission(Manifest.permission.RECORD_AUDIO, callback)
}

/**
 * 申请电话权限
 * @param callback 权限申请回调
 */
fun FragmentActivity.requestPhonePermission(callback: PermissionCallback) {
    requestPermissions(arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE
    ), callback)
}

/**
 * 申请联系人权限
 * @param callback 权限申请回调
 */
fun FragmentActivity.requestContactsPermission(callback: PermissionCallback) {
    requestPermissions(arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS
    ), callback)
}

/**
 * 申请日历权限
 * @param callback 权限申请回调
 */
fun FragmentActivity.requestCalendarPermission(callback: PermissionCallback) {
    requestPermissions(arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    ), callback)
}

/**
 * 申请短信权限
 * @param callback 权限申请回调
 */
fun FragmentActivity.requestSmsPermission(callback: PermissionCallback) {
    requestPermissions(arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS
    ), callback)
}

/**
 * 检查权限是否已授予
 * @param permission 权限名称
 * @return 是否已授予
 */
fun FragmentActivity.isPermissionGranted(permission: String): Boolean {
    return PermissionManager.Companion.getInstance().isPermissionGranted(this, permission)
}

/**
 * 检查所有权限是否已授予
 * @param permissions 权限列表
 * @return 是否所有权限都已授予
 */
fun FragmentActivity.areAllPermissionsGranted(permissions: Array<String>): Boolean {
    return PermissionManager.Companion.getInstance().areAllPermissionsGranted(this, permissions)
}

/**
 * 清理权限Fragment
 */
fun FragmentActivity.cleanupPermissionFragment() {
    PermissionManager.Companion.getInstance().cleanupFragment(this)
} 