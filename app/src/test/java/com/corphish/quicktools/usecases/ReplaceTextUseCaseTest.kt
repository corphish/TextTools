package com.corphish.quicktools.usecases

import androidx.compose.ui.text.TextRange
import com.corphish.quicktools.repository.TextReplacementRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ReplaceTextUseCaseTest {

    private lateinit var useCase: ReplaceTextUseCase
    private val repository: TextReplacementRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        useCase = ReplaceTextUseCase(repository)
    }

    @Test
    fun testReplaceOne() {
        val range = TextRange(0, 5)
        val newText = "hi"
        
        useCase.replaceOne(range, newText)
        
        verify { repository.replaceOne(range, newText) }
    }

    @Test
    fun testReplaceAll() {
        val oldText = "hello"
        val newText = "hi"
        val ignoreCase = true
        
        useCase.replaceAll(oldText, newText, ignoreCase)
        
        verify { repository.replaceAll(oldText, newText, ignoreCase) }
    }
}
