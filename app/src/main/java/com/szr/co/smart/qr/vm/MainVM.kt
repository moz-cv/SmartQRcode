package com.szr.co.smart.qr.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szr.co.smart.qr.http.HttpLogic
import com.szr.co.smart.qr.logic.IntentLogic
import com.szr.co.smart.qr.model.VideoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainVM : ViewModel() {

    private var mJob: Job? = null
    var mVideo: VideoModel? = null
    val status = MutableLiveData<Int>()


    fun checkPushVideoParse() {
        val url = IntentLogic.instance.getVideoUrl()
        if (url.isNullOrEmpty()) return
        parseQRVideo(url)
    }

    private fun parseQRVideo(url: String) {
        status.postValue(0)
        mJob?.cancel()
        mJob = viewModelScope.launch(Dispatchers.IO) {
            mVideo = HttpLogic.startParseVideoByUrl(url)
            status.postValue(1)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mJob?.cancel()
        mJob = null
    }
}