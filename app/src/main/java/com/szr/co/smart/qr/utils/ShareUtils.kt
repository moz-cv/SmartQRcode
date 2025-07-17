package com.szr.co.smart.qr.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareUtils {
    /**
     * 分享文本
     */
    fun shareText(context: Context, text: String, title: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        val chooser = Intent.createChooser(intent, title)
        context.startActivity(chooser)
    }

    /**
     * 分享Bitmap图片
     * @param authority FileProvider的authority（如 context.packageName + ".fileprovider"）
     */
    fun shareBitmap(context: Context, bitmap: Bitmap, title: String) {
        // 保存到临时文件
        val file = File(context.cacheDir, "share_qr_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri: Uri =
            FileProvider.getUriForFile(context, "${context.packageName}.FileProvider", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val chooser = Intent.createChooser(intent, title)
        context.startActivity(chooser)
    }

    /**
     * 分享多张Bitmap图片
     * @param bitmaps 图片列表
     * @param title 分享面板标题
     */
    fun shareBitmaps(context: Context, bitmaps: List<Bitmap>, title: String) {
        val uris = ArrayList<Uri>()
        for (bitmap in bitmaps) {
            val file = File(context.cacheDir, "share_multi_${System.currentTimeMillis()}_${uris.size}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.FileProvider", file)
            uris.add(uri)
        }
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "image/*"
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val chooser = Intent.createChooser(intent, title)
        context.startActivity(chooser)
    }
} 