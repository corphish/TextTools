package com.corphish.quicktools.repository

import android.content.Context
import android.net.Uri
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * Uri based text repository for file writes
 */
class TextRepositoryImpl(val context: Context): TextRepository {
    override fun writeText(uri: Uri?, text: String): Boolean {
        if (uri == null) {
            return false
        }

        val outputStream: OutputStream?
        try {
            outputStream = context.contentResolver.openOutputStream(uri)
            val bw = BufferedWriter(OutputStreamWriter(outputStream))
            bw.write(text)
            bw.flush()
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        return true
    }
}