package com.corphish.quicktools.functions

import com.corphish.quicktools.data.TemplateType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TextTemplateFunctionsTest {
    private lateinit var functions: TextTemplateFunctions

    @Before
    fun setUp() {
        functions = TextTemplateFunctions()
    }

    @Test
    fun testApplyTemplate() {
        assertEquals("Hello Android", functions.applyTemplate("Hello %s", "Android", TemplateType.PLAIN_TEXT))
    }

    @Test
    fun testApplyTemplate_URL() {
        // Testing URL encoding. Robolectric should handle Uri.encode
        assertEquals("https://google.com/search?q=Hello%20Android", functions.applyTemplate("https://google.com/search?q=%s", "Hello Android", TemplateType.URL))
    }

    @Test
    fun testApplyTemplate_Escaped() {
        // %%s should be treated as literal %s
        assertEquals("Literal %s with Android", functions.applyTemplate("Literal %%s with %s", "Android", TemplateType.PLAIN_TEXT))
    }

    @Test
    fun testApplyTemplate_MultiplePlaceholders() {
        // Only the first one should be replaced if it's not recursive, 
        // but replace("%s", input) replaces all occurrences.
        assertEquals("Android and Android", functions.applyTemplate("%s and %s", "Android", TemplateType.PLAIN_TEXT))
    }
}
