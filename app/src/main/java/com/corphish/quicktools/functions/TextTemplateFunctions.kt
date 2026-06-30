package com.corphish.quicktools.functions

import android.net.Uri
import com.corphish.quicktools.data.TemplateType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextTemplateFunctions @Inject constructor() {
    fun applyTemplate(template: String, input: String, type: TemplateType): String {
        val processedInput = if (type == TemplateType.URL) {
            Uri.encode(input)
        } else {
            input
        }

        return template
            .replace("%%s", "\u0000")
            .replace("%s", processedInput)
            .replace("\u0000", "%s")
    }
}
