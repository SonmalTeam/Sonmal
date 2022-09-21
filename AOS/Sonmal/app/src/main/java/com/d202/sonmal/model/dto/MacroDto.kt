package com.d202.sonmal.model.dto

data class MacroDto(
    var seq:Int,
    var categorySeq: Int,
    var title: String,
    var content: String,
    var macroCategory: String,
    var signSrc: String,
    var icon: String,
    var count: Int = 0
    ) {
}