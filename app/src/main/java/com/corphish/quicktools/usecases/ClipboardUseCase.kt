package com.corphish.quicktools.usecases

import com.corphish.quicktools.functions.ContextFunctions
import javax.inject.Inject

class ClipboardUseCase @Inject constructor(
    private val contextFunctions: ContextFunctions
) {
    fun copyToClipboard(text: String) {
        contextFunctions.copyToClipboard(text)
    }
}