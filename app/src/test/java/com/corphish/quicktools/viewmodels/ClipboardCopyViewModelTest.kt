package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.usecases.ClipboardUseCase
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ClipboardCopyViewModelTest {

    private lateinit var viewModel: ClipboardCopyViewModel
    private val clipboardUseCase: ClipboardUseCase = mockk(relaxed = true)

    @Before
    fun setUp() {
        viewModel = ClipboardCopyViewModel(clipboardUseCase)
    }

    @Test
    fun testCopyToClipboard() {
        val text = "test text"
        viewModel.copyToClipboard(text)
        verify { clipboardUseCase.copyToClipboard(text) }
    }
}
