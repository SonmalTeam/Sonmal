package com.d202.sonmal.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.d202.sonmal.databinding.ItemChatBinding
import com.d202.sonmal.model.dto.Chat

class ChatAdapter: Adapter<ChatAdapter.ViewHolder>() {
    var list = listOf<Chat>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    inner class ViewHolder(private val binding: ItemChatBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = list.size
}