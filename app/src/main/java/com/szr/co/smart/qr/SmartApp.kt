package com.szr.co.smart.qr

import android.app.Application

class SmartApp : Application() {

    companion object {
        lateinit var instance: SmartApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}