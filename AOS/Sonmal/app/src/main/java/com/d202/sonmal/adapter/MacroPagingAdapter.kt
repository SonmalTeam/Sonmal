package com.d202.sonmal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.d202.sonmal.R
import com.d202.sonmal.databinding.ItemMacroRecyclerBinding
import com.d202.sonmal.model.dto.MacroDto

//diffCallback: DiffUtil.ItemCallback<MacroDto>
class MacroPagingAdapter() : PagingDataAdapter<MacroDto,
        MacroPagingAdapter.MPagingViewHolder>(IMAGE_COMPARATOR) {

    class MPagingViewHolder(val binding: ItemMacroRecyclerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MacroDto) {
            binding.data = item
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
            holder.binding.apply {
                imgSpeak.setOnClickListener {
                    speakClickListener.onClick(it, position, currentItem)
                }
                tvEmoji.setOnClickListener {
                    videoClickListener.onClick(it, position, currentItem)
                }
                tvTitle.setOnClickListener {
                    titleItemClickListener.onClick(it, position, currentItem)
                }

                if(currentItem.videoFileId != 0) {
                    tvEmoji.setBackgroundResource(R.drawable.playbutton)
                }
            }
        }


    }

    // ItemClickListener 세팅
    interface LongItemClickListener { // TTS
        fun onClick(view: View, position: Int, item: MacroDto)
    }
    private lateinit var longItemClickListener: LongItemClickListener

    fun setLongItemClickListener(itemClickListener: LongItemClickListener) {
        this.longItemClickListener = itemClickListener
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

    companion object {
        private val IMAGE_COMPARATOR = object : DiffUtil.ItemCallback<MacroDto>() {
            override fun areItemsTheSame(oldItem: MacroDto, newItem: MacroDto) =
                oldItem.seq == newItem.seq

            override fun areContentsTheSame(oldItem: MacroDto, newItem: MacroDto) =
                oldItem == newItem
        }
    }

}

