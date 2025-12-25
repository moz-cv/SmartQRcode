package com.szr.co.smart.qr.http

import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.gson.Gson
import com.szr.co.smart.qr.BuildConfig
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.data.DataSetting
import com.szr.co.smart.qr.event.AppEvent
import com.szr.co.smart.qr.model.VideoModel
import kotlinx.coroutines.delay
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object HttpLogic {

    private val httpClient = OkHttpClient.Builder().apply {
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            addInterceptor(httpLoggingInterceptor)
        }
    }.build()

    private val vaRequestUrl: String
        get() {
            return if (BuildConfig.DEBUG) {
                "https://test.vibedownloader.shop/kkcc/lingan/"
            } else {
                "https://api.vibedownloader.shop/kkcc/lingan/"
            }
        }

    private val tokenUrl: String
        get() {
            return if (BuildConfig.DEBUG) {
                "https://ftest.vibedownloader.shop/kkcc/cok/" // test
            } else {
                "https://fcm.vibedownloader.shop/kkcc/cok/" // release
            }
        }

    fun startParseVideoByUrl(url: String?): VideoModel? {
        try {
            if (url.isNullOrEmpty()) {
                return null
            }
            event("sq_pv_start")
            val urlBuilder = vaRequestUrl.toHttpUrlOrNull()!!.newBuilder()
            urlBuilder.addQueryParameter("c-t", url)
            val finalUrl: String = urlBuilder.build().toString()
            val builder = Request.Builder().url(finalUrl)
            builder.addHeader("HFF", SmartApp.instance.packageName)
            val request = builder.build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val code = response.code
                if (code == 200) {
                    val body = response.body?.string()
                    if (body.isNullOrEmpty()) {
                        event("sq_pv_fail", "fail1")
                        return null
                    }
                    val json = JSONObject(body)
                    val code = json.getInt("code")
                    if (code != 0) {
                        event("sq_pv_fail", "api fail:$code")
                        return null
                    }
                    val resultData = try {
                        Gson().fromJson(
                            json.getJSONObject("data").toString(),
                            VideoModel::class.java
                        )
                    } catch (_: Exception) {
                        null
                    }
                    if (resultData == null) {
                        event("sq_pv_fail", "fail2")
                        return null
                    }
                    event("sq_pv_successful")
                    return resultData
                } else {
                    event("sm_pv_fail", "fail service:$code")
                    return null
                }
            }
            event("sq_pv_fail", "fail3")
            return null
        } catch (_: Exception) {
        }
        event("sq_pv_fail", "fail4")
        return null
    }

    fun uploadToken(token: String?): Boolean {
        event("sq_upload_token")
        try {
            val result = httpClient.newCall(createRequest(token)).execute()
            if (result.isSuccessful) {
                DataSetting.instance.fcmTokenUploadTime = System.currentTimeMillis()
                if (!token.isNullOrEmpty()) DataSetting.instance.fcmToken = token
                event("sq_upload_token_suc")
                return true
            } else {
                event("sq_upload_token_fail", "fail:${result.code}")
                return false
            }
        } catch (e: Exception) {
            event("sq_upload_token_fail", "fail:${e.message}")
            return false
        }
    }

    fun sendClock(): Boolean {

        val builder = Request.Builder().url("https://fcm.vibedownloader.shop/kkcc/ka/")
        builder.addHeader("HFF", SmartApp.instance.packageName)
        builder.addHeader("YT", BuildConfig.VERSION_NAME)
        val request = builder.build()
        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val success = response.code == 200
            if (success) {
                return true
            }
        }
        return false
    }

    private fun createRequest(token: String?): Request {
        return Request.Builder().url(tokenUrl).addHeader("HFF", SmartApp.instance.packageName)
            .addHeader("YT", BuildConfig.VERSION_NAME).post(createRequestBody(token))
            .build()
    }


    private fun createRequestBody(token: String?): RequestBody {
        val bodyJson = JSONObject().apply {
            put("dg-z", getGoogleId())
            put("y-an", DataSetting.instance.installTime)
            if (!token.isNullOrEmpty()) {
                put("didi", token)
            }
            val ref = DataSetting.instance.referValue
            if (!ref.isNullOrEmpty()) {
                put("gfvalue", ref)
            }
        }
        return bodyJson.toString().toRequestBody("application/json".toMediaType())
    }

    private fun getGoogleId(): String? {
        return try {
            val googleIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(SmartApp.instance)
            val googleId = googleIdInfo.id
            googleId ?: ""
        } catch (_: Error) {
            ""
        }
    }


    private fun event(event: String, value: String? = null) {
        if (value.isNullOrEmpty()) {
            AppEvent.event(event)
        } else {
            AppEvent.eventValue(event, value)
        }
    }
}