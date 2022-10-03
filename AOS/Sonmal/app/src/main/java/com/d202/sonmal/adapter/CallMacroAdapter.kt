package com.d202.sonmal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.d202.sonmal.databinding.ItemCallMacroBinding

class CallMacroAdapter: Adapter<CallMacroAdapter.ViewHolder>() {
    var list = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    inner class ViewHolder(private val binding: ItemCallMacroBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(text: String){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemCallMacroBinding =
            ItemCallMacroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemCallMacroBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}