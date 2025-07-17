package com.szr.co.smart.qr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.databinding.ItemLanguageBinding
import com.szr.co.smart.qr.utils.AppLangUtils


class AppLangAdapter : RecyclerView.Adapter<AppLangAdapter.ViewHolder>() {

    interface Callback {
        fun onItemClick(position: Int)
    }

    var callback: Callback? = null
    var checkedIndex = -1
    private val dataList = mutableListOf<AppLangUtils.Language>()


    fun refreshData(list: List<AppLangUtils.Language>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        var displayName = data.displayName
        if (displayName === AppLangUtils.Language.Auto.displayName) {
            holder.itemView.context.getString(R.string.auto)
        }
        holder.binding.tvName.text = displayName
        val check = checkedIndex == position
        holder.binding.root.setBackgroundResource(if (check) R.drawable.shape_language_selected else R.drawable.shape_language)
        holder.binding.tvName.setTextColor(holder.itemView.context.getColor(if (check) R.color.white else R.color.col_767676))
    }

    inner class ViewHolder(val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                callback?.onItemClick(bindingAdapterPosition)
                val lastIndex = checkedIndex
                checkedIndex = bindingAdapterPosition
                notifyItemChanged(lastIndex)
                notifyItemChanged(checkedIndex)
            }
        }
    }
}