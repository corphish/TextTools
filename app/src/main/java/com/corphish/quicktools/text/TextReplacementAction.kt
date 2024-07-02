package com.corphish.quicktools.text

data class TextReplacementAction(
    // Old text that was replaced
    val oldText: String,

    // New text that the old text was replaced with
    val newText: String,
)
