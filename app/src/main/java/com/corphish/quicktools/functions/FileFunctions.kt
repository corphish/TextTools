package com.corphish.quicktools.functions

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import javax.inject.Inject
import androidx.core.net.toUri

/**
 * Class containing file related functions that require context.
 */
class FileFunctions @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Saves text to the given URI string.
     * @param uriString String representation of the URI.
     * @param text Text to save.
     * @return Boolean indicating success.
     */
    fun saveTextToUri(uriString: String, text: String): Boolean {
        if (uriString.isEmpty()) return false
        val uri = uriString.toUri()

        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { bw ->
                    bw.write(text)
                    bw.flush()
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}
