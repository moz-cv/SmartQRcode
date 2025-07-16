package com.szr.co.smart.qr.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.CodaBarWriter
import com.google.zxing.oned.Code128Writer
import com.google.zxing.oned.Code39Writer
import com.google.zxing.oned.Code93Writer
import com.google.zxing.oned.EAN13Writer
import com.google.zxing.oned.EAN8Writer
import com.google.zxing.oned.ITFWriter
import com.google.zxing.oned.UPCAWriter
import com.google.zxing.oned.UPCEWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.king.zxing.util.CodeUtils
import com.szr.co.smart.qr.model.QRCodeType


object QrUtils {


    fun generateQRCode(
        content: String, format: BarcodeFormat
    ): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val hints = mapOf(EncodeHintType.MARGIN to 1) // 设置边距为1
        val bitMatrix: BitMatrix
        try {
            bitMatrix = qrCodeWriter.encode(content, format, 600, 600, hints)
        } catch (e: WriterException) {
            e.printStackTrace()
            return createBitmap(600, 600, Bitmap.Config.RGB_565).apply {
                eraseColor(Color.WHITE)
            }
        }

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return bitmap
    }


    fun generateBarCode(content: String, format: BarcodeFormat?): Bitmap {
        // 选择正确的 Writer
        val codeWriter = when (format) {
            BarcodeFormat.ITF -> ITFWriter()
            BarcodeFormat.CODE_39 -> Code39Writer()
            BarcodeFormat.CODE_93 -> Code93Writer()
            BarcodeFormat.CODE_128 -> Code128Writer()
            BarcodeFormat.CODABAR -> CodaBarWriter()
            BarcodeFormat.UPC_A -> UPCAWriter()
            BarcodeFormat.UPC_E -> UPCEWriter()
            BarcodeFormat.EAN_8 -> EAN8Writer()
            BarcodeFormat.EAN_13 -> EAN13Writer()
            else -> Code128Writer()
        }


        val width = 600
        val height = 300
        val bitMatrix: BitMatrix
        try {
            bitMatrix = codeWriter.encode(content, format, width, height)
        } catch (e: WriterException) {
            e.printStackTrace()
            return createBitmap(width, height, Bitmap.Config.RGB_565).apply {
                eraseColor(Color.WHITE)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return createBitmap(width, height, Bitmap.Config.RGB_565).apply {
                eraseColor(Color.WHITE)
            }
        }

        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return bitmap
    }


    fun convertType(type: BarcodeFormat, content: String): Int {
        return when (type) {
            BarcodeFormat.CODABAR -> QRCodeType.BARCODE_CODABAR
            BarcodeFormat.CODE_39 -> QRCodeType.BARCODE_CODE_39
            BarcodeFormat.CODE_93 -> QRCodeType.BARCODE_CODE_93
            BarcodeFormat.CODE_128 -> QRCodeType.BARCODE_CODE_128
            BarcodeFormat.EAN_8 -> QRCodeType.BARCODE_EAN_8
            BarcodeFormat.EAN_13 -> QRCodeType.BARCODE_EAN_13
            BarcodeFormat.ITF -> QRCodeType.BARCODE_ITF
            BarcodeFormat.UPC_A -> QRCodeType.BARCODE_UPC_A
            BarcodeFormat.UPC_E -> QRCodeType.BARCODE_UPC_E
            BarcodeFormat.QR_CODE -> detectQRCodeType(content)
            else -> QRCodeType.QRCODE_TEXT
        }
    }


    fun convertTypeZxingType(type: Int): BarcodeFormat {
        return when (type) {
            QRCodeType.BARCODE_CODABAR -> BarcodeFormat.CODABAR
            QRCodeType.BARCODE_CODE_39 -> BarcodeFormat.CODE_39
            QRCodeType.BARCODE_CODE_93 -> BarcodeFormat.CODE_93
            QRCodeType.BARCODE_CODE_128 -> BarcodeFormat.CODE_128
            QRCodeType.BARCODE_EAN_8 -> BarcodeFormat.EAN_8
            QRCodeType.BARCODE_EAN_13 -> BarcodeFormat.EAN_13
            QRCodeType.BARCODE_ITF -> BarcodeFormat.ITF
            QRCodeType.BARCODE_UPC_A -> BarcodeFormat.UPC_A
            QRCodeType.BARCODE_UPC_E -> BarcodeFormat.UPC_E
            else -> BarcodeFormat.QR_CODE
        }
    }


    /**
     * 判断二维码内容的类型
     * @param content 二维码内容字符串
     * @return 二维码类型枚举
     */
    fun detectQRCodeType(content: String): Int {
        return when {
            isWebsite(content) -> QRCodeType.QRCODE_WEBSITE
            isEmail(content) -> QRCodeType.QRCODE_EMAIL
            isContact(content) -> QRCodeType.QRCODE_CONTACT
            isPhone(content) -> QRCodeType.QRCODE_PHONE
            isWifi(content) -> QRCodeType.QRCODE_WIFI
            isTwitter(content) -> QRCodeType.QRCODE_X
            isFacebook(content) -> QRCodeType.QRCODE_FACEBOOK
            isWhatsApp(content) -> QRCodeType.QRCODE_WHATSAPP
            isInstagram(content) -> QRCodeType.QRCODE_INS
            else -> QRCodeType.QRCODE_TEXT
        }
    }

    private fun isWebsite(content: String): Boolean {
        val websiteRegex =
            """^(https?|ftp)://[^\s/$.?#].[^\s]*${'$'}""".toRegex(RegexOption.IGNORE_CASE)
        return websiteRegex.matches(content) ||
                content.startsWith("www.", ignoreCase = true) ||
                content.matches(
                    """^[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,}(:[0-9]{1,5})?(/.*)?${'$'}""".toRegex(
                        RegexOption.IGNORE_CASE
                    )
                )
    }

    private fun isEmail(content: String): Boolean {
        val emailRegex =
            """^mailto:([a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,})${'$'}""".toRegex()
        return content.matches(emailRegex) ||
                content.matches("""^([a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,})${'$'}""".toRegex())
    }

    private fun isContact(content: String): Boolean {
        return content.startsWith("BEGIN:VCARD", ignoreCase = true) ||
                content.startsWith("MECARD:", ignoreCase = true)
    }

    private fun isPhone(content: String): Boolean {
        val phoneRegex = """^tel:(\+?\d[\d\s-]*)${'$'}""".toRegex()
        return phoneRegex.matches(content) ||
                content.matches("""^\+?\d[\d\s-]*${'$'}""".toRegex())
    }

    private fun isWifi(content: String): Boolean {
        return content.startsWith("WIFI:", ignoreCase = true)
    }

    fun isTwitter(content: String): Boolean {
        return content.startsWith("https://twitter.com/", ignoreCase = true) ||
                content.startsWith("https://x.com/", ignoreCase = true) ||
                content.startsWith("twitter.com/", ignoreCase = true) ||
                content.startsWith("x.com/", ignoreCase = true)
    }

    fun isFacebook(content: String): Boolean {
        return content.startsWith("https://www.facebook.com/", ignoreCase = true) ||
                content.startsWith("www.facebook.com/", ignoreCase = true) ||
                content.startsWith("fb.me/", ignoreCase = true) ||
                content.startsWith("https://fb.me/", ignoreCase = true)
    }

    fun isWhatsApp(content: String): Boolean {
        return content.startsWith("https://wa.me/", ignoreCase = true) ||
                content.startsWith("wa.me/", ignoreCase = true) ||
                content.startsWith("https://api.whatsapp.com/", ignoreCase = true) ||
                content.startsWith("api.whatsapp.com/", ignoreCase = true)
    }

    fun isInstagram(content: String): Boolean {
        return content.startsWith("https://www.instagram.com/", ignoreCase = true) ||
                content.startsWith("www.instagram.com/", ignoreCase = true) ||
                content.startsWith("instagr.am/", ignoreCase = true) ||
                content.startsWith("https://instagr.am/", ignoreCase = true)
    }
}