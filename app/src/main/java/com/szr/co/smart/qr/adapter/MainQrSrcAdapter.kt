package com.szr.co.smart.qr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.ItemQrSrcBinding
import com.szr.co.smart.qr.logic.QrResLogic

class MainQrSrcAdapter(val list: List<Int>, val itemClick: (Int) -> Unit) :
    RecyclerView.Adapter<MainQrSrcAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemQrSrcBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val id = list[position]
        val resId = QrResLogic.getResById(id)
        if (resId == -1) {
            holder.binding.icImage.setImageResource(R.drawable.shape_default_image)
        } else {
            val options = RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
            Glide.with(holder.binding.root)
                .load(resId)
                .apply(options)
                .into(holder.binding.icImage)
        }
        holder.binding.root.setOnClickListener {
            itemClick.invoke(id)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemQrSrcBinding) : RecyclerView.ViewHolder(binding.root)
}