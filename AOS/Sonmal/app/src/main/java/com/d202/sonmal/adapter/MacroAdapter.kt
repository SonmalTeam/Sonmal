package com.d202.sonmal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
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

        holder.binding.apply {
            imgSpeak.setOnClickListener {
                speakClickListener.onClick(it, position, item)
            }
            tvEmoji.setOnClickListener {
                videoClickListener.onClick(it, position, item)
            }
            tvTitle.setOnClickListener {
                titleItemClickListener.onClick(it, position, item)
            }
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    // ItemClickListener 세팅
    interface SpeakItemClickListener { // TTS
        fun onClick(view: View, position: Int, item: MacroDto)
    }
    private lateinit var speakClickListener: SpeakItemClickListener

    fun setSpeakClickListener(itemClickListener: SpeakItemClickListener) {
        this.speakClickListener = itemClickListener
    }

    // ItemClickListener 세팅
    interface VideoItemClickListener { // video 재생
        fun onClick(view: View, position: Int, item: MacroDto)
    }
    private lateinit var videoClickListener: VideoItemClickListener

    fun setVideoClickListener(itemClickListener: VideoItemClickListener) {
        this.videoClickListener = itemClickListener
    }

    // ItemClickListener 세팅
    interface TitleItemClickListener { // video 재생
        fun onClick(view: View, position: Int, item: MacroDto)
    }
    private lateinit var titleItemClickListener: TitleItemClickListener

    fun setTitleClickListener(itemClickListener: TitleItemClickListener) {
        this.titleItemClickListener = itemClickListener
    }

}

class MacroViewHolder(val binding: ItemMacroRecyclerBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MacroDto) {
        binding.tvTitle.text = item.title
        if(item.icon != null) {
            binding.tvEmoji.text = item.icon
        }else {
//            binding.imgThumbnail = item.signSrc
        }
    }

}

