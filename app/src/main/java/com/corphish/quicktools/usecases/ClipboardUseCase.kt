package com.corphish.quicktools.usecases

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import com.corphish.quicktools.R
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ClipboardUseCase @Inject constructor(
    @ActivityContext private val context: Context
) {
    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text_tools_result", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_LONG).show()
    }
}