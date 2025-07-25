package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.ActivityParseResultBinding
import com.szr.co.smart.qr.model.VideoModel
import androidx.core.net.toUri
import com.szr.co.smart.qr.activity.base.BaseAdActivity
import com.szr.co.smart.qr.bill.ViBillHelper
import com.szr.co.smart.qr.bill.position.ViBillPosition


class ParseResultActivity : BaseAdActivity<ActivityParseResultBinding>() {


    companion object {
        fun toScanResult(context: Context, data: VideoModel?) {
            if (data == null) return
            val intent = Intent(context, ParseResultActivity::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }
    }

    override val billHelper: ViBillHelper by lazy {
        ViBillHelper(
            this,
            ViBillPosition.POS_QR_PARSE_RESULT_INTERS,
            mutableListOf(ViBillPosition.POS_MAIN_CLICK_INTERS, ViBillPosition.POS_MAIN_NATIVE),
            ViBillPosition.POS_QR_PARSE_RESULT_NATIVE,
            mBinding.layoutNativeAd
        )
    }

    override fun inflateBinding(): ActivityParseResultBinding {
        return ActivityParseResultBinding.inflate(layoutInflater)

    }

    override fun initOnCreate() {
        super.initOnCreate()
        val data = intent.getParcelableExtra<VideoModel>("data")
        if (data != null) {
            try {
                val options =
                    RequestOptions().priority(Priority.NORMAL).format(DecodeFormat.PREFER_ARGB_8888)
                Glide.with(this).load(data.img).apply(options)
                    .placeholder(R.drawable.shape_image)
                    .into(mBinding.image)
            } catch (_: Exception) {
            }
        }

        mBinding.layoutVideo.setOnClickListener {
            if (data != null) {
                billHelper.showAd {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(data.url.toUri(), "video/*")
                    startActivity(intent)
                }
            }
        }
    }
}