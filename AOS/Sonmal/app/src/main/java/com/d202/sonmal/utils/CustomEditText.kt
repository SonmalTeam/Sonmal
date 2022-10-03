package com.d202.sonmal.utils

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.util.AttributeSet
import android.widget.Toast

class CustomEditText : androidx.appcompat.widget.AppCompatEditText {

    private lateinit var contextC: Context
    private var hasEmoji = 0

    constructor(context: Context?) : super(context!!) {
        contextC = context
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        contextC = context
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        contextC = context
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
                    Toast.makeText(contextC, "이모티콘만 입력이 가능합니다.", Toast.LENGTH_SHORT).show()
                    return ""
                }
            }

            hasEmoji = 1
            return null
        }
    }
}