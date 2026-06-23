package com.corphish.quicktools.functions

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextTemplateFunctions @Inject constructor() {
    fun applyTemplate(template: String, input: String): String {
        return template
            .replace("%%s", "\u0000")
            .replace("%s", input)
            .replace("\u0000", "%s")
    }
}
