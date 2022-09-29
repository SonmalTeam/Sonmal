package com.d202.sonmal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.d202.sonmal.databinding.ItemResultBinding

class VoiceAdapter(private val itemList: List<String>): RecyclerView.Adapter<VoiceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceViewHolder {
        val voiceBinding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VoiceViewHolder(voiceBinding)
    }

    override fun onBindViewHolder(holder: VoiceViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

}

class  VoiceViewHolder(val binding: ItemResultBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item : String) {
        binding.tvResult.text = item
    }
}