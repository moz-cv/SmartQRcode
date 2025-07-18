package com.szr.co.smart.qr.bill.cache

import com.szr.co.smart.qr.bill.info.ViBaseBill

class ViBillCache {

    private val billList = mutableListOf<ViBaseBill>()

    fun size(): Int {
        synchronized(billList) {
            val iterator = billList.iterator()
            while (iterator.hasNext()) {
                if (!iterator.next().isCanShow) {
                    iterator.remove()
                }
            }
            return billList.size
        }
    }

    fun add(bill: ViBaseBill?) {
        if (bill == null) return
        synchronized(billList) {
            billList.add(bill)
            billList.sortByDescending { it.keyInfo.priority }
        }
    }

    fun get(): ViBaseBill? {
        synchronized(billList) {
            val iterator = billList.iterator()
            while (iterator.hasNext()) {
                val bill = iterator.next()
                if (bill.isCanShow) {
                    return bill
                } else {
                    iterator.remove()
                }
            }
            return null
        }
    }
}