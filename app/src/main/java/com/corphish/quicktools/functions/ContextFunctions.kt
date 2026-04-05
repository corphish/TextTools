package com.corphish.quicktools.functions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Class to perform functions that involve context.
 * No UI related stuff should be performed here.
 */
class ContextFunctions @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text_tools_result", text)
        clipboard.setPrimaryClip(clip)
    }

    fun openInWeb(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = url.toUri()
        context.startActivity(browserIntent)
    }
}