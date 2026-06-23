package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.TextCountResult
import com.corphish.quicktools.repository.TextAnalysisRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextCountUseCaseTest {

    private lateinit var useCase: TextCountUseCase
    private val repository: TextAnalysisRepository = mockk()

    @Before
    fun setUp() {
        useCase = TextCountUseCase(repository)
    }

    @Test
    fun testExecute() = runTest {
        val text = "hello"
        val expectedResult = TextCountResult(characterCount = 5)
        coEvery { repository.analyzeText(text) } returns expectedResult

        val result = useCase.execute(text)

        assertEquals(expectedResult, result)
    }
}
