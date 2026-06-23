package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.functions.IntentAction
import com.corphish.quicktools.functions.ParsedTextAction
import com.corphish.quicktools.usecases.TextActionDeterminationUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextActionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TextActionViewModel
    private val useCase: TextActionDeterminationUseCase = mockk(relaxed = true)

    @Before
    fun setUp() {
        viewModel = TextActionViewModel(useCase)
    }

    @Test
    fun testDetermineActions() = runTest {
        val actions = listOf(ParsedTextAction(IntentAction.COPY_TO_CLIPBOARD, "hello"))
        coEvery { useCase.buildTextActions("hello", true) } returns actions

        viewModel.determineActions("hello", true)

        val state = viewModel.textActionsFlow.value
        assertTrue(state is TexActionState.Result)
        assertEquals(actions, (state as TexActionState.Result).actions)
    }

    @Test
    fun testPerformAction() {
        val action = ParsedTextAction(IntentAction.COPY_TO_CLIPBOARD, "hello")
        viewModel.performAction(action)
        verify { useCase.performAction(action) }
    }
}
