package com.d202.sonmal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.d202.sonmal.databinding.ItemDialBinding

class DialAdapter: Adapter<DialAdapter.ViewHolder>() {
    init {
        notifyDataSetChanged()
    }
    private val list = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")
    inner class ViewHolder(private val binding: ItemDialBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: String){
            binding.apply {
                tvDial.text = item
                constDial.setOnClickListener {
                    onClickDialListener.onClick(item)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemDialBinding = ItemDialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemDialBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    lateinit var onClickDialListener: OnClickDialListener
    interface OnClickDialListener{
        fun onClick(dial: String)
    }
}