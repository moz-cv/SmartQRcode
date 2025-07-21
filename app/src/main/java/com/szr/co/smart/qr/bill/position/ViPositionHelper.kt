package com.szr.co.smart.qr.bill.position

import com.szr.co.smart.qr.bill.info.ViBaseBill
import com.szr.co.smart.qr.conf.FireRemoteConf
import com.szr.co.smart.qr.manager.UserManager
import com.szr.co.smart.qr.utils.Utils
import org.json.JSONObject

object ViPositionHelper {

    private val posStateMap = hashMapOf<String, Int>()

    fun openBillPosition(position: ViBillPosition): Boolean {
        return when (getPositionState(position)) {
            0 -> {
                //非渠道用户
                !UserManager.instance.buyUser()
            }

            1 -> {
                //渠道用户
                UserManager.instance.buyUser()
            }

            2 -> {
                //全部打开
                true
            }

            else -> {
                //关闭
                false
            }
        }
    }

    //TODO 需要修改-banner 使用不管
    fun refreshBillPosition(position: ViBillPosition): Boolean {
//        return when (getPositionState(position)) {
//            0 -> {
//                !UserTypeManger.instance.isChannelUser()
//            }
//
//            1, 3 -> {
//                UserTypeManger.instance.isChannelUser()
//            }
//
//            2 -> {
//                true
//            }
//
//            else -> {
//                false
//            }
//        }
        return true
    }

    fun clean() {
        posStateMap.clear()
    }


    fun getBill(position: ViBillPosition): ViBaseBill? {
        if (!openBillPosition(position)) return null
        return position.type.getBill()
    }


    private fun getPositionState(position: ViBillPosition): Int {
        val state = posStateMap[position.position]
        if (state != null) return state
        val config = Utils.decodeBase64(FireRemoteConf.instance.adpConfig)
        try {
            val sta = JSONObject(config).getInt(position.position)
            posStateMap[position.position] = sta
            return sta
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

}