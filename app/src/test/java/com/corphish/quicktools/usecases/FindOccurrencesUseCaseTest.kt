package com.corphish.quicktools.usecases

import androidx.compose.ui.text.TextRange
import com.corphish.quicktools.functions.TextFunctions
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FindOccurrencesUseCaseTest {

    private lateinit var useCase: FindOccurrencesUseCase
    private val textFunctions: TextFunctions = mockk()

    @Before
    fun setUp() {
        useCase = FindOccurrencesUseCase(textFunctions)
    }

    @Test
    fun testExecute() {
        val mainInput = "hello world"
        val findText = "hello"
        val ignoreCase = false
        val expectedRanges = listOf(TextRange(0, 5))

        every { textFunctions.findOccurrences(mainInput, findText, ignoreCase) } returns expectedRanges

        val result = useCase.execute(mainInput, findText, ignoreCase)

        assertEquals(expectedRanges, result)
    }
}
