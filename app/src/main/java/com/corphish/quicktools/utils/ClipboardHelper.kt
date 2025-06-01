package com.corphish.quicktools.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.CLIPBOARD_SERVICE
import com.corphish.quicktools.R

/**
 * Helper to copy to clipboard
 */
object ClipboardHelper {
    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text_tools_result", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_LONG).show()
    }
}