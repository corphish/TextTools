package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.NumberAnalysisResult
import com.corphish.quicktools.repository.NumberAnalysisRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class NumberAnalysisUseCaseTest {

    private val repository: NumberAnalysisRepository = mockk()
    private val useCase = NumberAnalysisUseCase(repository)

    @Test
    fun testExecute() {
        val text = "123"
        val expectedResult = mockk<NumberAnalysisResult>()
        every { repository.analyze(text, null, 10) } returns expectedResult

        val result = useCase.execute(text, null, 10)

        assertEquals(expectedResult, result)
        verify { repository.analyze(text, null, 10) }
    }
}
