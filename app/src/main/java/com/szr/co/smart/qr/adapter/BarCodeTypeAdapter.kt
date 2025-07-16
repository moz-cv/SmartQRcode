package com.szr.co.smart.qr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.szr.co.smart.qr.databinding.ItemBarTypeBinding
import com.szr.co.smart.qr.databinding.ItemQrTypeBinding
import com.szr.co.smart.qr.model.BarCodeTypeModel
import com.szr.co.smart.qr.model.QRCodeType
import com.szr.co.smart.qr.model.QRCodeTypeModel

class BarCodeTypeAdapter(val list: List<BarCodeTypeModel>, val callback: (type: Int) -> Unit) :
    RecyclerView.Adapter<BarCodeTypeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemBarTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val data = list[position]
        holder.binding.tvTitle.setText(data.titleResId)

        holder.binding.root.setOnClickListener {
            callback.invoke(data.type)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemBarTypeBinding) : RecyclerView.ViewHolder(binding.root)
}