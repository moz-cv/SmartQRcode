package com.szr.co.smart.qr.utils.permission

/**
 * 权限申请回调接口
 */
interface PermissionCallback {
    
    /**
     * 权限申请成功
     * @param permissions 被授予的权限列表
     */
    fun onPermissionGranted( permissions: Array<String>)
    
    /**
     * 权限申请被拒绝
     * @param deniedPermissions 被拒绝的权限列表
     */
    fun onPermissionDenied(deniedPermissions: Array<String>)
    
    /**
     * 权限被永久拒绝（用户选择了"不再询问"）
     * @param permanentlyDeniedPermissions 被永久拒绝的权限列表
     */
    fun onPermissionPermanentlyDenied(permanentlyDeniedPermissions: Array<String>)
} 