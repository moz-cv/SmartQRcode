package com.szr.co.smart.qr.model


data class PushModel(
    val id: Int,
    val content: String,
    val action: String,
    val url: String,
    val coverUrl: String,
)