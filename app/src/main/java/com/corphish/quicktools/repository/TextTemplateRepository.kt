package com.corphish.quicktools.repository

import com.corphish.quicktools.data.TextTemplate

interface TextTemplateRepository {
    fun getTemplates(): List<TextTemplate>
    fun saveTemplate(template: TextTemplate)
    fun deleteTemplate(id: String)
    fun updateTemplate(template: TextTemplate)
    fun applyTemplate(template: String, input: String): String
    fun clearAllTemplates()
}
