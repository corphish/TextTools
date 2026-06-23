package com.corphish.quicktools.functions

import android.view.textclassifier.TextClassifier
import android.view.textclassifier.TextLinks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextActionDeterminationFunctionsTest {
    private lateinit var functions: TextActionDeterminationFunctions
    private val textClassifier: TextClassifier = mockk()

    @Before
    fun setUp() {
        functions = TextActionDeterminationFunctions()
        mockkConstructor(TextLinks.Request.Builder::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testApplyAction() {
        val result = functions.applyAction("hello")
        assertEquals(IntentAction.APPLY, result.type)
        assertEquals("hello", result.parsedText)
    }

    @Test
    fun testCopyToClipboardAction() {
        val result = functions.copyToClipboardAction("hello")
        assertEquals(IntentAction.COPY_TO_CLIPBOARD, result.type)
        assertEquals("hello", result.parsedText)
    }

    @Test
    fun testDetermineTextActions() = runTest {
        val text = "Contact me at test@example.com"
        val request = mockk<TextLinks.Request>()
        val links = mockk<TextLinks>()
        val link = mockk<TextLinks.TextLink>()

        every { anyConstructed<TextLinks.Request.Builder>().build() } returns request
        every { textClassifier.generateLinks(request) } returns links
        every { links.links } returns listOf(link)
        every { link.getEntity(0) } returns TextClassifier.TYPE_EMAIL
        every { link.getConfidenceScore(TextClassifier.TYPE_EMAIL) } returns 0.9f
        every { link.start } returns 14
        every { link.end } returns 30

        val result = functions.determineTextActions(text, textClassifier)

        assertEquals(1, result.size)
        assertEquals(IntentAction.EMAIL, result[0].type)
        assertEquals("test@example.com", result[0].parsedText)
    }
}
