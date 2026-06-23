package com.corphish.quicktools.repository

import android.view.textclassifier.TextClassifier
import com.corphish.quicktools.functions.ContextFunctions
import com.corphish.quicktools.functions.IntentAction
import com.corphish.quicktools.functions.ParsedTextAction
import com.corphish.quicktools.functions.TextActionDeterminationFunctions
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextActionRepositoryImplTest {
    private lateinit var repository: TextActionRepositoryImpl
    private val contextFunctions: ContextFunctions = mockk(relaxed = true)
    private val textActionDeterminationFunctions: TextActionDeterminationFunctions = mockk()
    private val textClassifier: TextClassifier = mockk()

    @Before
    fun setUp() {
        every { contextFunctions.textClassifier() } returns textClassifier
        repository = TextActionRepositoryImpl(contextFunctions, textActionDeterminationFunctions)
    }

    @Test
    fun testBuildTextActions() = runTest {
        val text = "hello"
        val applyAction = ParsedTextAction(IntentAction.APPLY, text)
        val copyAction = ParsedTextAction(IntentAction.COPY_TO_CLIPBOARD, text)
        val emailAction = ParsedTextAction(IntentAction.EMAIL, "test@test.com")

        every { textActionDeterminationFunctions.applyAction(text) } returns applyAction
        every { textActionDeterminationFunctions.copyToClipboardAction(text) } returns copyAction
        coEvery { textActionDeterminationFunctions.determineTextActions(text, textClassifier) } returns listOf(emailAction)

        val result = repository.buildTextActions(text, true)

        assertEquals(3, result.size)
        assertTrue(result.contains(applyAction))
        assertTrue(result.contains(copyAction))
        assertTrue(result.contains(emailAction))
    }

    @Test
    fun testPerformTextAction_Email() {
        val action = ParsedTextAction(IntentAction.EMAIL, "test@test.com")
        every { contextFunctions.sendEmail(any()) } returns true
        
        val result = repository.performTextAction(action)
        
        assertTrue(result)
        verify { contextFunctions.sendEmail("test@test.com") }
    }

    @Test
    fun testPerformTextAction_CopyToClipboard() {
        val action = ParsedTextAction(IntentAction.COPY_TO_CLIPBOARD, "hello")
        
        val result = repository.performTextAction(action)
        
        assertTrue(result)
        verify { contextFunctions.copyToClipboard("hello") }
    }
}
