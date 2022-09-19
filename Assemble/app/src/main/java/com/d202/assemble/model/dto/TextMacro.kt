package com.d202.assemble.model.dto

data class TextMacro(
    var userSeq:Int,
    var macroSeq: Int,
    var macroTitle: String,
    var macroContent: String,
    var macroCategory: String) {
}