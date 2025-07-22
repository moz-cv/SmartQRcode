package com.szr.co.smart.qr.logic

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.conf.FireRemoteConf
import com.szr.co.smart.qr.event.AppEvent
import com.szr.co.smart.qr.http.HttpLogic
import com.szr.co.smart.qr.model.VideoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.URL
import kotlin.let
import kotlin.text.isNullOrEmpty


class IntentLogic {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { IntentLogic() }
    }

    private var url: String? = null

    private var mJob: Job? = null
    var mVideo: VideoModel? = null
    val status = MutableLiveData<Int>()

    fun parseIntentData(intent: Intent) {
        val action = intent.action
        if (action == "VIDEO_PUSH") {
            url = intent.getStringExtra("url")
            AppEvent.event("sq_noti_cli")
            checkPushVideoParse()
        }
    }

    fun checkPushVideoParse() {
        if (url.isNullOrEmpty()) return
        parseQRVideo(url!!)
    }


    fun checkPushVideoParseGuide() {
        val url = FireRemoteConf.instance.guideDefaultUrl
        parseQRVideo(url)
    }

    private fun parseQRVideo(url: String) {
        status.postValue(0)
        mJob?.cancel()
        mJob = SmartApp.instance.scope.launch(Dispatchers.IO) {
            mVideo = HttpLogic.startParseVideoByUrl(url)
            if (mVideo != null) status.postValue(1) else status.postValue(2)
        }
    }
}