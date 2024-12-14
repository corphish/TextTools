package com.corphish.quicktools.repository

import android.net.Uri

/**
 * Common definition for text repository capabilities for file writes.
 */
interface TextRepository {
    /**
     * Writes the given text to somewhere.
     * @return Boolean indicating success state.
     */
    fun writeText(uri: Uri?, text: String): Boolean
}