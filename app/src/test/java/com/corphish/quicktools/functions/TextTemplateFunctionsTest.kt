package com.corphish.quicktools.functions

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TextTemplateFunctionsTest {
    private lateinit var functions: TextTemplateFunctions

    @Before
    fun setUp() {
        functions = TextTemplateFunctions()
    }

    @Test
    fun testApplyTemplate() {
        assertEquals("Hello Android", functions.applyTemplate("Hello %s", "Android"))
    }

    @Test
    fun testApplyTemplate_Escaped() {
        // %%s should be treated as literal %s
        assertEquals("Literal %s with Android", functions.applyTemplate("Literal %%s with %s", "Android"))
    }

    @Test
    fun testApplyTemplate_MultiplePlaceholders() {
        // Only the first one should be replaced if it's not recursive, 
        // but replace("%s", input) replaces all occurrences.
        // Let's see how I implemented it.
        // It uses .replace("%s", input) which replaces all.
        assertEquals("Android and Android", functions.applyTemplate("%s and %s", "Android"))
    }
}
