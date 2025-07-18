package com.szr.co.smart.qr.logic

import com.google.firebase.messaging.FirebaseMessaging
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.data.DataSetting
import com.szr.co.smart.qr.http.HttpLogic
import com.szr.co.smart.qr.utils.Utils
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object PushTokenLogic {

    private var uploadFlag = false
    private var completeCheckUpload = false

    fun uploadToken(token: String? = null) {
        if (uploadFlag) {
            completeCheckUpload = true
            return
        }
        uploadFlag = true

        SmartApp.instance.scope.launch {
            var uploadToken = token
            if (uploadToken.isNullOrEmpty()) {
                uploadToken = getToken()
            }
            val isSameDay = Utils.sameDay(DataSetting.instance.fcmTokenUploadTime)
            if (DataSetting.instance.fcmToken.isNullOrEmpty() && uploadToken.isNullOrEmpty()) {
                if (isSameDay) {
                    uploadFlag = false
                    return@launch
                }
            } else {
                if (DataSetting.instance.fcmToken == uploadToken && isSameDay) {
                    uploadFlag = false
                    return@launch
                }
            }
            HttpLogic.uploadToken(uploadToken)
            uploadFlag = false
            if (completeCheckUpload) {
                delay(1200)
                withContext(Dispatchers.Main) {
                    completeCheckUpload = false
                    uploadToken()
                }
            }
        }
    }

    private suspend fun getToken(): String? {
        val deferred = CompletableDeferred<String?>()
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                deferred.complete(it.result)
            } else {
                deferred.complete(null)
            }
        }
        return deferred.await()
    }
}