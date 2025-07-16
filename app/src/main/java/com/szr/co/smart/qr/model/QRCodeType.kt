package com.szr.co.smart.qr.model


@Retention(AnnotationRetention.SOURCE)
annotation class QRCodeType {
    companion object {
        val QRCODE_WEBSITE = 0
        val QRCODE_EMAIL = 1
        val QRCODE_CONTACT = 2
        val QRCODE_PHONE = 3
        val QRCODE_TEXT = 4
        val QRCODE_WIFI = 5
        val QRCODE_X = 6
        val QRCODE_FACEBOOK = 7
        val QRCODE_INS = 8
        val QRCODE_WHATSAPP = 9
        val BARCODE_ITF = 10
        val BARCODE_CODABAR = 11
        val BARCODE_CODE_39 = 12
        val BARCODE_CODE_93 = 13
        val BARCODE_CODE_128 = 14
        val BARCODE_UPC_A = 15
        val BARCODE_UPC_E = 16
        val BARCODE_EAN_8 = 17
        val BARCODE_EAN_13 = 18

    }
}