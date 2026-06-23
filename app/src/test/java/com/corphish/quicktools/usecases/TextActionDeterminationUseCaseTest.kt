package com.corphish.quicktools.usecases

import com.corphish.quicktools.functions.IntentAction
import com.corphish.quicktools.functions.ParsedTextAction
import com.corphish.quicktools.repository.TextActionRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextActionDeterminationUseCaseTest {
    private lateinit var useCase: TextActionDeterminationUseCase
    private val repository: TextActionRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        useCase = TextActionDeterminationUseCase(repository)
    }

    @Test
    fun testBuildTextActions() = runTest {
        val actions = listOf(ParsedTextAction(IntentAction.COPY_TO_CLIPBOARD, "hello"))
        coEvery { repository.buildTextActions("hello", true) } returns actions
        
        val result = useCase.buildTextActions("hello", true)
        
        assertEquals(actions, result)
    }

    @Test
    fun testPerformAction() {
        val action = ParsedTextAction(IntentAction.COPY_TO_CLIPBOARD, "hello")
        useCase.performAction(action)
        verify { repository.performTextAction(any()) }
    }
}
