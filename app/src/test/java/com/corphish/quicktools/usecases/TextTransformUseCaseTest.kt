package com.corphish.quicktools.usecases

import com.corphish.quicktools.repository.TextTransformRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TextTransformUseCaseTest {

    private lateinit var useCase: TextTransformUseCase
    private val repository: TextTransformRepository = mockk()

    @Before
    fun setUp() {
        useCase = TextTransformUseCase(repository)
    }

    @Test
    fun testExecute() {
        val text = "hello"
        val primaryIndex = 1
        val secondaryIndex = 2
        val secondaryText = "wrap"
        val expectedResult = "(hello)"

        every { 
            repository.transform(text, primaryIndex, secondaryIndex, secondaryText) 
        } returns expectedResult

        val actualResult = useCase.execute(text, primaryIndex, secondaryIndex, secondaryText)

        assertEquals(expectedResult, actualResult)
        verify { repository.transform(text, primaryIndex, secondaryIndex, secondaryText) }
    }
}
