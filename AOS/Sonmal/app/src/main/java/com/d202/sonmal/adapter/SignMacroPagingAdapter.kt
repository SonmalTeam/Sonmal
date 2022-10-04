package com.d202.sonmal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.d202.sonmal.databinding.ItemCallMacroBinding
import com.d202.sonmal.databinding.ItemSignLangMacroBinding
import com.d202.sonmal.model.dto.MacroDto

class SignMacroPagingAdapter: PagingDataAdapter<MacroDto, SignMacroPagingAdapter.ViewHolder>(IMAGE_COMPARATOR) {
    inner class ViewHolder(private val binding: ItemSignLangMacroBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: MacroDto){
            binding.data = item
            binding.constMacro.setOnClickListener {
                onItemMacroClickListener.onClick(item.content)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null) {
            holder.bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemSignLangMacroBinding = ItemSignLangMacroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemSignLangMacroBinding)
    }

    lateinit var onItemMacroClickListener: OnItemMacroClickListener
    interface OnItemMacroClickListener{
        fun onClick(content: String)
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

