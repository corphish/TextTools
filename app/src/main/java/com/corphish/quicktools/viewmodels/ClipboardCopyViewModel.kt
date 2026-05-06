package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import com.corphish.quicktools.usecases.ClipboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// ViewModel that supports copy to clipboard usecase
@HiltViewModel
open class ClipboardCopyViewModel @Inject constructor(
    private val clipboardUseCase: ClipboardUseCase
): ViewModel() {
    fun copyToClipboard(text: String) {
        clipboardUseCase.copyToClipboard(text)
    }
}