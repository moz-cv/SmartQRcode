package com.szr.co.smart.qr.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoModel(
    @SerializedName("c-t") val url: String,
    @SerializedName("i-t") val img: String?,
    @SerializedName("d-t") val duration: Double,
    @SerializedName("t-t") val title: String?,
    @SerializedName("minoor") val author: String?,
    @SerializedName("ween") val avatar: String?,
    @SerializedName("h-t") val header: HashMap<String, String>?,
    @SerializedName("cdec") val uploaderUrl: String?
): Parcelable