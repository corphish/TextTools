package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.TextCountResult
import com.corphish.quicktools.functions.TextFunctions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class TextCountUseCaseTest {

    private val textFunctions: TextFunctions = mockk()
    private val useCase = TextCountUseCase(textFunctions)

    @Test
    fun testExecute() {
        val text = "test"
        val expectedResult = TextCountResult(characterCount = 4)
        every { textFunctions.countText(text) } returns expectedResult

        val result = useCase.execute(text)

        assertEquals(expectedResult, result)
        verify { textFunctions.countText(text) }
    }
}
