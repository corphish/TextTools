package com.corphish.quicktools.repository

import android.content.Context
import com.corphish.quicktools.data.TemplateType
import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.functions.TextTemplateFunctions
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class TextTemplateRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val textTemplateFunctions: TextTemplateFunctions
) : TextTemplateRepository {
    private val prefs = context.getSharedPreferences("text_templates", Context.MODE_PRIVATE)
    private val key = "templates"

    override fun getTemplates(): List<TextTemplate> {
        val json = prefs.getString(key, "[]") ?: "[]"
        val array = JSONArray(json)
        val list = mutableListOf<TextTemplate>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                TextTemplate(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    template = obj.getString("template"),
                    type = if (obj.has("type")) TemplateType.valueOf(obj.getString("type")) else TemplateType.PLAIN_TEXT
                )
            )
        }
        return list
    }

    override fun saveTemplate(template: TextTemplate) {
        val templates = getTemplates().toMutableList()
        templates.add(template)
        saveList(templates)
    }

    override fun deleteTemplate(id: String) {
        val templates = getTemplates().filter { it.id != id }
        saveList(templates)
    }

    override fun updateTemplate(template: TextTemplate) {
        val templates = getTemplates().map {
            if (it.id == template.id) template else it
        }
        saveList(templates)
    }

    override fun applyTemplate(template: String, input: String, type: TemplateType): String {
        return textTemplateFunctions.applyTemplate(template, input, type)
    }

    override fun clearAllTemplates() {
        prefs.edit {
            remove(key)
        }
    }

    private fun saveList(list: List<TextTemplate>) {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("name", it.name)
            obj.put("template", it.template)
            obj.put("type", it.type.name)
            array.put(obj)
        }
        prefs.edit {
            putString(key, array.toString())
        }
    }
}
