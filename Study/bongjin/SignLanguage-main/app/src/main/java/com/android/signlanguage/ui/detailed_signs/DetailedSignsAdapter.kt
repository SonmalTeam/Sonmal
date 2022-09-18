package com.android.signlanguage.ui.detailed_signs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.signlanguage.R

class DetailedSignsAdapter(private val dataSet: List<Pair<Int, Int>>) :
    RecyclerView.Adapter<DetailedSignsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val signImage: ImageView = view.findViewById(R.id.sign_image)
        val detailedSignImage: ImageView = view.findViewById(R.id.detailed_sign_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detailed_signs, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.signImage.setImageResource(dataSet[position].first)
        holder.detailedSignImage.setImageResource(dataSet[position].second)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}