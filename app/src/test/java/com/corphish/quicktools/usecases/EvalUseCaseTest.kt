package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.Result
import com.corphish.quicktools.repository.EvalRepository
import com.corphish.quicktools.viewmodels.EvalViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EvalUseCaseTest {

    private lateinit var useCase: EvalUseCase
    private val repository: EvalRepository = mockk()

    @Before
    fun setUp() {
        useCase = EvalUseCase(repository)
    }

    @Test
    fun testExecute() {
        every { repository.evaluate("3,14 * 2", 2) } returns Result.Success("6.28")

        val result = useCase.execute("3,14 * 2", 2)

        assertEquals("6.28", (result as Result.Success).value)
        verify { repository.evaluate("3,14 * 2", 2) }
    }

    @Test
    fun testBuildOutput_Replace() {
        assertEquals("4", useCase.buildOutput("2 + 2", "4", EvalViewModel.EVAL_RESULT_REPLACE))
    }

    @Test
    fun testBuildOutput_Append() {
        assertEquals("2 + 2 = 4", useCase.buildOutput("2 + 2", "4", EvalViewModel.EVAL_RESULT_APPEND))
    }
}
