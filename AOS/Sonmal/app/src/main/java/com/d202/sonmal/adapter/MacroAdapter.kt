package com.d202.sonmal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.d202.sonmal.R
import com.d202.sonmal.databinding.ItemMacroRecyclerBinding
import com.d202.sonmal.model.dto.MacroDto

class MacroAdapter(private val itemList: List<MacroDto>): RecyclerView.Adapter<MacroViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MacroViewHolder {
        val macroBinding = ItemMacroRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MacroViewHolder(macroBinding)
    }

    override fun onBindViewHolder(holder: MacroViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}

class MacroViewHolder(val binding: ItemMacroRecyclerBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MacroDto) {
        binding.tvTitle.text = item.title
        if(item.signSrc != null) {
//            binding.imgThumbnail = item.icon
        }else {
//            binding.imgThumbnail = item.signSrc
        }
    }

}

