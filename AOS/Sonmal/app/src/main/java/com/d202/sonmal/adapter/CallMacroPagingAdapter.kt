package com.d202.sonmal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.d202.sonmal.databinding.ItemCallMacroBinding
import com.d202.sonmal.model.dto.MacroDto

class CallMacroPagingAdapter: PagingDataAdapter<MacroDto, CallMacroPagingAdapter.ViewHolder>(IMAGE_COMPARATOR) {
    inner class ViewHolder(private val binding: ItemCallMacroBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: MacroDto){
            binding.data = item
            binding.constMacro.setOnClickListener {
                onItemMacroClickListener.onClick(item.title)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null) {
            holder.bind(item!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemCallMacroBinding = ItemCallMacroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemCallMacroBinding)
    }

    lateinit var onItemMacroClickListener: OnItemMacroClickListener
    interface OnItemMacroClickListener{
        fun onClick(title: String)
    }

    companion object {
        private val IMAGE_COMPARATOR = object : DiffUtil.ItemCallback<MacroDto>() {
            override fun areItemsTheSame(oldItem: MacroDto, newItem: MacroDto) =
                oldItem.seq == newItem.seq

            override fun areContentsTheSame(oldItem: MacroDto, newItem: MacroDto) =
                oldItem == newItem
        }
    }
}

