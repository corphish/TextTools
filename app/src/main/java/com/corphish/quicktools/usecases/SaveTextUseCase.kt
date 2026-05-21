package com.corphish.quicktools.usecases

import com.corphish.quicktools.repository.TextRepository
import javax.inject.Inject

/**
 * Use case to save text to a file.
 * This class is pure Kotlin and has no Android dependencies.
 */
class SaveTextUseCase @Inject constructor(
    private val textRepository: TextRepository
) {
    /**
     * Saves text to the given destination.
     * @param uriString String representation of the destination URI.
     * @param text Text to save.
     * @return Boolean indicating success.
     */
    fun execute(uriString: String, text: String): Boolean {
        return textRepository.writeText(uriString, text)
    }
}
