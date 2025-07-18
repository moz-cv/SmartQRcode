package com.szr.co.smart.qr.bill.info

class ViBillInfo(val limit: Int, val keyInfoList: MutableList<ViKeyInfo>) {
    var max: Int = 5
    var min: Int = 3
}