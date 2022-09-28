package com.d202.sonmal.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.d202.sonmal.databinding.ItemMacroRecyclerBinding
import com.d202.sonmal.model.dto.MacroDto

//diffCallback: DiffUtil.ItemCallback<MacroDto>
class MacroPagingAdapter(val activity: Activity) : PagingDataAdapter<MacroDto,
        MacroPagingAdapter.MPagingViewHolder>(IMAGE_COMPARATOR) {

    class MPagingViewHolder(private val binding: ItemMacroRecyclerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(macrodto: MacroDto) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MPagingViewHolder {
        val binding = ItemMacroRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MPagingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MPagingViewHolder, position: Int) {
        val currentItem = getItem(position)

        if(currentItem != null) {
            holder.bind(currentItem)
        }
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

