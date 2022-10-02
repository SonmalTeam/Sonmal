package com.d202.sonmal.utils

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.util.AttributeSet

class CustomEditText : androidx.appcompat.widget.AppCompatEditText {

    private var hasEmoji = 0

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        filters = arrayOf(EmojiIncludeFilter())
    }

    private inner class EmojiIncludeFilter : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            if(this@CustomEditText.text.toString() == "") {
                hasEmoji = 0
            }
            if(this@CustomEditText.text.toString() != "" && hasEmoji == 1) {
                return ""
            }
            for (i in start until end) {
                val type = Character.getType(source[i])
                if (type != Character.SURROGATE.toInt() && type != Character.OTHER_SYMBOL.toInt()) {
                    // Other then emoji value will be blank
                    return ""
                }
            }

            hasEmoji = 1
            return null
        }
    }
}