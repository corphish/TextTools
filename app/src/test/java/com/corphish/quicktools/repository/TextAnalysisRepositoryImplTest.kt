package com.corphish.quicktools.repository

import android.view.textclassifier.TextClassifier
import com.corphish.quicktools.data.TextCountResult
import com.corphish.quicktools.functions.ContextFunctions
import com.corphish.quicktools.functions.TextClassifierFunctions
import com.corphish.quicktools.functions.TextFunctions
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextAnalysisRepositoryImplTest {

    private lateinit var repository: TextAnalysisRepositoryImpl
    private val textFunctions: TextFunctions = mockk()
    private val textClassifierFunctions: TextClassifierFunctions = mockk()
    private val contextFunctions: ContextFunctions = mockk()
    private val textClassifier: TextClassifier = mockk()

    @Before
    fun setUp() {
        every { contextFunctions.textClassifier() } returns textClassifier
        repository = TextAnalysisRepositoryImpl(textFunctions, textClassifierFunctions, contextFunctions)
    }

    @Test
    fun testAnalyzeText() = runTest {
        val text = "Contact test@example.com"
        val baseResult = TextCountResult(characterCount = text.length)
        
        every { textFunctions.countText(text) } returns baseResult
        coEvery { textClassifierFunctions.extractEntities(text, textClassifier) } returns mapOf(
            TextClassifier.TYPE_EMAIL to listOf("test@example.com")
        )

        val result = repository.analyzeText(text)

        assertEquals(text.length, result.characterCount)
        assertEquals(listOf("test@example.com"), result.emails)
    }
}
