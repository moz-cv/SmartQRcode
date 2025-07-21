package com.szr.co.smart.qr.model

import com.szr.co.smart.qr.manager.UserManager
import com.szr.co.smart.qr.model.AppSwitchType.all
import com.szr.co.smart.qr.model.AppSwitchType.buser
import com.szr.co.smart.qr.model.AppSwitchType.normal

enum class AppSwitchType {
    normal, buser, all
}

fun checkAppSwitchType(code: Int): Boolean {
    return when (code) {
        normal.ordinal -> !UserManager.instance.buyUser()
        buser.ordinal -> UserManager.instance.buyUser()
        all.ordinal -> true
        else -> false
    }
}