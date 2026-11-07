package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import com.corphish.quicktools.usecases.ClipboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Base ViewModel for screens that copy values to the clipboard.
 *
 * @param clipboardUseCase Performs the copy.
 */
@HiltViewModel
open class ClipboardCopyViewModel @Inject constructor(
    private val clipboardUseCase: ClipboardUseCase
): ViewModel() {
    /** Copies [text] to the clipboard. */
    fun copyToClipboard(text: String) {
        clipboardUseCase.copyToClipboard(text)
    }
}