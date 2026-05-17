package com.corphish.quicktools.usecases

import com.corphish.quicktools.functions.ContextFunctions
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ClipboardUseCaseTest {

    private lateinit var clipboardUseCase: ClipboardUseCase
    private val contextFunctions: ContextFunctions = mockk(relaxed = true)

    @Before
    fun setUp() {
        clipboardUseCase = ClipboardUseCase(contextFunctions)
    }

    @Test
    fun testCopyToClipboard() {
        val text = "hello"
        clipboardUseCase.copyToClipboard(text)
        verify { contextFunctions.copyToClipboard(text) }
    }
}
