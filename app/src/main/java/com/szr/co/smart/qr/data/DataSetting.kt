package com.szr.co.smart.qr.data

import android.app.Application
import com.tencent.mmkv.MMKV

class DataSetting {

    private val mmkv by lazy { MMKV.defaultMMKV() }

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DataSetting() }
    }

    fun initialize(application: Application) {
        MMKV.initialize(application)
        if (installTime == 0L) {
            installTime = System.currentTimeMillis()
        }
    }

    /**
     *安装时间
     */
    var installTime: Long
        get() {
            return mmkv.decodeLong("sq_installTime", 0)
        }
        set(value) {
            mmkv.encode("sq_installTime", value)
        }


    var vipUser: Boolean
        get() {
            return mmkv.decodeBool("sq_user")
        }
        set(value) {
            mmkv.encode("sq_user", value)
        }


    var autoVip: Boolean
        get() {
            return mmkv.decodeBool("sq_auto_vip")
        }
        set(value) {
            mmkv.encode("sq_auto_vip", value)
        }

    /**
     * fcm 上传成功时间
     */
    var fcmTokenUploadTime: Long
        get() {
            return mmkv.decodeLong("sq_token_uploadTime", 0)
        }
        set(value) {
            mmkv.encode("sq_token_uploadTime", value)
        }

    /**
     * fcm token
     */
    var fcmToken: String?
        get() {
            return mmkv.decodeString("sq_token")
        }
        set(value) {
            mmkv.encode("sq_token", value)
        }

    /**
     * installRefer 值
     */
    var referValue: String?
        get() {
            return mmkv.decodeString("sq_infer_data")
        }
        set(value) {
            mmkv.encode("sq_infer_data", value)
        }

    /**
     * installReferrer 是否完成
     */
    var installReferrerFinish: Boolean
        get() {
            return mmkv.decodeBool("sq_infer_finish", false)
        }
        set(value) {
            mmkv.encode("sq_infer_finish", value)
        }

    /**
     * fb 广告累积价值
     */
    var fbAdPaid: String?
        get() {
            return mmkv.decodeString("sq_fd_paid")
        }
        set(value) {
            mmkv.encode("sq_fd_paid", value)
        }


    /**
     * se 广告累积价值
     */
    var seAdPaid: String?
        get() {
            return mmkv.decodeString("sq_se_paid")
        }
        set(value) {
            mmkv.encode("sq_se_paid", value)
        }

    /**
     * 累加价值是否上传
     */
    var adPaidReturnUpload: Boolean
        get() {
            return mmkv.decodeBool("sq_ad_paid_upload", false)
        }
        set(value) {
            mmkv.encode("sq_ad_paid_upload", value)
        }

    /**
     * 累加价值
     */
    var adPaidReturn: Long
        get() {
            return mmkv.decodeLong("sq_ad_paid", 0L)
        }
        set(value) {
            mmkv.encode("sq_ad_paid", value)
        }

    /**
     * app 首次引导
     */
    var langGuide: Boolean
        get() {
            return mmkv.decodeBool("sq_langGuide")
        }
        set(value) {
            mmkv.encode("sq_langGuide", value)
        }

    var pushIdIndex: Int
        get() {
            return mmkv.decodeInt("sq_pushid_index", 0)
        }
        set(value) {
            mmkv.encode("sq_pushid_index", value)
        }

    /**
     * 首次接受到fcm 推送的时间
     */
    var fcmTime: Long
        get() {
            return mmkv.decodeLong("sq_fcm_time", 0)
        }
        set(value) {
            mmkv.encode("sq_fcm_time", value)
        }

    var notifyHitDialogLastTime: Long
        get() {
            return mmkv.decodeLong("sq_post_per_time", 0)
        }
        set(value) {
            mmkv.encode("sq_post_per_time", value)
        }

    /**
     * app 首次引导二维码
     */
    var firstGuideQr: Boolean
        get() {
            return mmkv.decodeBool("sq_guide_qr")
        }
        set(value) {
            mmkv.encode("sq_guide_qr", value)
        }
}