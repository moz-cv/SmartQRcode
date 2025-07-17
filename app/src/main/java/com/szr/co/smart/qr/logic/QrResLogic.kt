package com.szr.co.smart.qr.logic

import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.model.QRCodeType

object QrResLogic {

    val listBgImages = arrayListOf<Int>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

    fun getResById(id: Int): Int {
        return when (id) {
            1 -> R.drawable.ic_qr_bg1
            2 -> R.drawable.ic_qr_bg2
            3 -> R.drawable.ic_qr_bg3
            4 -> R.drawable.ic_qr_bg4
            5 -> R.drawable.ic_qr_bg5
            6 -> R.drawable.ic_qr_bg6
            7 -> R.drawable.ic_qr_bg7
            8 -> R.drawable.ic_qr_bg8
            9 -> R.drawable.ic_qr_bg9
            10 -> R.drawable.ic_qr_bg10
            11 -> R.drawable.ic_qr_bg11
            12 -> R.drawable.ic_qr_bg12
            13 -> R.drawable.ic_qr_bg13
            14 -> R.drawable.ic_qr_bg14
            15 -> R.drawable.ic_qr_bg15
            else -> -1
        }
    }

    fun getQRCodeTypeRes(type: Int): Pair<Int, Int>? {
        return when (type) {
            QRCodeType.QRCODE_WEBSITE -> Pair(R.mipmap.ic_qr_website, R.string.website)
            QRCodeType.QRCODE_CONTACT -> Pair(R.mipmap.ic_qr_contact, R.string.contact)
            QRCodeType.QRCODE_PHONE -> Pair(R.mipmap.ic_qr_phone, R.string.phone)
            QRCodeType.QRCODE_TEXT -> Pair(R.mipmap.ic_qr_text, R.string.text)
            QRCodeType.QRCODE_WIFI -> Pair(R.mipmap.ic_qr_wifi, R.string.wifi)
            QRCodeType.QRCODE_X -> Pair(R.mipmap.ic_qr_x, R.string.x)
            QRCodeType.QRCODE_FACEBOOK -> Pair(R.mipmap.ic_qr_facebook, R.string.facebook)
            QRCodeType.QRCODE_INS -> Pair(R.mipmap.ic_qr_ins, R.string.ins)
            QRCodeType.QRCODE_WHATSAPP -> Pair(R.mipmap.ic_qr_wapp, R.string.whatsapp)
            QRCodeType.QRCODE_EMAIL -> Pair(R.mipmap.ic_qr_email, R.string.email)
            else -> null
        }
    }


    fun getQRCodeTypeResName(type: Int): Int? {
        return when (type) {
            QRCodeType.QRCODE_WEBSITE -> R.string.website
            QRCodeType.QRCODE_CONTACT -> R.string.contact
            QRCodeType.QRCODE_PHONE -> R.string.phone
            QRCodeType.QRCODE_TEXT -> R.string.text
            QRCodeType.QRCODE_WIFI -> R.string.wifi
            QRCodeType.QRCODE_X -> R.string.x
            QRCodeType.QRCODE_FACEBOOK -> R.string.facebook
            QRCodeType.QRCODE_INS -> R.string.ins
            QRCodeType.QRCODE_WHATSAPP -> R.string.whatsapp
            QRCodeType.QRCODE_EMAIL -> R.string.email
            QRCodeType.BARCODE_ITF -> R.string.barcode_itf
            QRCodeType.BARCODE_CODABAR -> R.string.barcode_codabar
            QRCodeType.BARCODE_CODE_39 -> R.string.barcode_code_39
            QRCodeType.BARCODE_CODE_93 -> R.string.barcode_code_93
            QRCodeType.BARCODE_CODE_128 -> R.string.barcode_code_128
            QRCodeType.BARCODE_UPC_A -> R.string.barcode_upc_a
            QRCodeType.BARCODE_UPC_E -> R.string.barcode_upc_e
            QRCodeType.BARCODE_EAN_8 -> R.string.barcode_ean_8
            QRCodeType.BARCODE_EAN_13 -> R.string.barcode_ean_13
            else -> null
        }
    }
}