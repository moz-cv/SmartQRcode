package com.szr.co.smart.qr.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.ItemQrDataBinding
import com.szr.co.smart.qr.logic.QrResLogic
import com.szr.co.smart.qr.room.model.QRDataModel
import com.szr.co.smart.qr.utils.QrUtils
import android.graphics.Bitmap
import androidx.core.view.isVisible
import com.szr.co.smart.qr.utils.LruCacheUtils
import com.google.zxing.BarcodeFormat
import com.szr.co.smart.qr.utils.TimeFormatUtils
import kotlinx.coroutines.*


class QRHistoryAdapter(val context: Context) :
    PagingDataAdapter<QRDataModel, QRHistoryAdapter.ViewHolder>(diffUtil) {

    interface OnItemDataListener {}

    private val adapterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    var selectEnable = false
    var selectIdList = mutableMapOf<Int, QRDataModel>()

    fun changeSelect(select: Boolean) {
        selectEnable = select
        if (!selectEnable) {
            selectIdList.clear()
        }
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemQrDataBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        @JvmStatic
        internal val diffUtil
            get() = object : DiffUtil.ItemCallback<QRDataModel>() {
                override fun areItemsTheSame(
                    oldItem: QRDataModel, newItem: QRDataModel
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: QRDataModel, newItem: QRDataModel
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data == null) return
        holder.binding.tvTypeName.setText(
            QrResLogic.getQRCodeTypeResName(data.type) ?: R.string.qr_code
        )
        holder.binding.tvContent.text = data.content

        val time = TimeFormatUtils.formatDateTime(data.addTime)
        val format = QrUtils.convertTypeZxingType(data.type)
        holder.binding.tvDes.text = if (format == BarcodeFormat.QR_CODE) {
            holder.binding.tvDes.context.getString(R.string.qrcode_value, time)
        } else {
            holder.binding.tvDes.context.getString(R.string.barcode_value, time)
        }

        val cacheKey = LruCacheUtils.getIdByString(data.content)
        holder.binding.ivQr.tag = cacheKey // 设置tag为最终id

        val cachedBitmap = LruCacheUtils.getId(cacheKey)
        if (cachedBitmap != null) {
            holder.binding.ivQr.setImageBitmap(cachedBitmap)
        } else {
            holder.binding.ivQr.setImageBitmap(null)
            adapterScope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    QrUtils.createQrBitmap(context, data.content, format, data.bgId)
                }
                LruCacheUtils.putId(cacheKey, bitmap)
                // 用tag判断，防止图片错位
                if (holder.binding.ivQr.tag == cacheKey) {
                    holder.binding.ivQr.setImageBitmap(bitmap)
                }
            }
        }

        if (selectEnable) {
            holder.binding.ivSelect.isVisible = true
            val selected = selectIdList.contains(data.id)
            holder.binding.ivSelect.setImageResource(if (selected) R.mipmap.ic_selected else R.mipmap.ic_unselect)
        }else{
            holder.binding.ivSelect.isVisible = false
        }

        holder.binding.root.setOnClickListener {
            if (!selectEnable) return@setOnClickListener
            if (selectIdList.containsKey(data.id)) {
                selectIdList.remove(data.id)
            } else {
                selectIdList.put(data.id,data)
            }
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQrDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
}