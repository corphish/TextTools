package com.corphish.quicktools.data

enum class TemplateType {
    PLAIN_TEXT,
    URL
}

data class TextTemplate(
    val id: String,
    val name: String,
    val template: String,
    val type: TemplateType = TemplateType.PLAIN_TEXT
)
