package com.android.signlanguage

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.android.signlanguage.model.Language

@BindingAdapter("sign")
fun bindSignImage(imgView: ImageView, sign: Char) {
    val drawable = Language.getDrawable(sign)
    imgView.setImageResource(drawable)
}