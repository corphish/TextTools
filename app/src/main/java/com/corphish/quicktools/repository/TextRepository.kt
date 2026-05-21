package com.corphish.quicktools.repository

/**
 * Common definition for text repository capabilities for file writes.
 * This interface is clean of Android-specific classes.
 */
interface TextRepository {
    /**
     * Writes the given text to the destination specified by the uriString.
     * @param uriString String representation of the destination (e.g. a URI).
     * @param text Text to write.
     * @return Boolean indicating success state.
     */
    fun writeText(uriString: String, text: String): Boolean
}
