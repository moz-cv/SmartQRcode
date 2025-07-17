package com.szr.co.smart.qr.utils.permission

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * 权限申请Fragment
 * 用于解耦权限申请逻辑，避免在Activity中直接处理
 */
class PermissionFragment : Fragment() {

    companion object {
        private const val TAG = "Fragment"

        /**
         * 创建权限申请Fragment实例
         * @param permissions 需要申请的权限列表
         * @return PermissionFragment实例
         */
        fun newInstance(): PermissionFragment {
            return PermissionFragment()
        }
    }

    private var permissionCallback: PermissionCallback? = null
    private var permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        PermissionLogger.d(TAG, "Permission result received: $permissionsResult")
        handlePermissionResult(permissionsResult)
    }

    /**
     * 设置权限回调
     */
    fun setPermissionCallback(callback: PermissionCallback) {
        this.permissionCallback = callback
    }

    /**
     * 开始申请权限
     */
    fun requestPermissions(permissions: Array<String>) {
        PermissionLogger.d(TAG, "requestPermissions called, permissions size: ${permissions.size}")

        if (permissions.isEmpty()) {
            PermissionLogger.w(TAG, "No permissions to request")
            return
        }
        requestPermissionsInternal(permissions)
    }

    /**
     * 内部权限申请方法
     */
    private fun requestPermissionsInternal(permissions: Array<String>) {
        PermissionLogger.d(TAG, "requestPermissionsInternal called")

        // 检查是否已经有权限
        val permissionsToRequest = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        PermissionLogger.d(TAG, "Permissions to request: ${permissionsToRequest.contentToString()}")

        if (permissionsToRequest.isEmpty()) {
            // 所有权限都已授予
            PermissionLogger.d(TAG, "All permissions already granted")
            permissionCallback?.onPermissionGranted(permissions)
        } else {
            // 申请权限
            PermissionLogger.d(TAG, "Launching permission request")
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    /**
     * 处理权限申请结果
     */
    private fun handlePermissionResult(permissionsResult: Map<String, Boolean>) {
        val grantedPermissions = mutableListOf<String>()
        val deniedPermissions = mutableListOf<String>()
        val permanentlyDeniedPermissions = mutableListOf<String>()
        
        // 获取所有请求的权限
        val allRequestedPermissions = permissionsResult.keys.toTypedArray()

        permissionsResult.forEach { (permission, isGranted) ->
            if (isGranted) {
                grantedPermissions.add(permission)
            } else {
                // 检查是否被永久拒绝
                if (shouldShowRequestPermissionRationale(permission)) {
                    deniedPermissions.add(permission)
                } else {
                    permanentlyDeniedPermissions.add(permission)
                }
            }
        }

        // 回调结果
        when {
            grantedPermissions.size == allRequestedPermissions.size -> {
                // 所有权限都授予了
                permissionCallback?.onPermissionGranted(allRequestedPermissions)
            }

            permanentlyDeniedPermissions.isNotEmpty() -> {
                // 有权限被永久拒绝
                permissionCallback?.onPermissionPermanentlyDenied(
                    permanentlyDeniedPermissions.toTypedArray()
                )
            }

            else -> {
                // 部分权限被拒绝
                permissionCallback?.onPermissionDenied(deniedPermissions.toTypedArray())
            }
        }
    }

    /**
     * 检查单个权限是否已授予
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 检查所有权限是否已授予
     */
    fun areAllPermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(it) }
    }
} 