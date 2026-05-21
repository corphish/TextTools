package com.corphish.quicktools.repository

import com.corphish.quicktools.functions.FileFunctions
import javax.inject.Inject

/**
 * Implementation of TextRepository.
 * Delegates Android-specific operations to FileFunctions.
 */
class TextRepositoryImpl @Inject constructor(
    private val fileFunctions: FileFunctions
): TextRepository {
    override fun writeText(uriString: String, text: String): Boolean {
        return fileFunctions.saveTextToUri(uriString, text)
    }
}
