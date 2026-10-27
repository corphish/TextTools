package com.corphish.quicktools.ui.common

import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Test

class TextTest {

    @Test
    fun testSplitForEmphasis_MultipleWords() {
        assertEquals("Find and" to "replace", splitForEmphasis("Find and replace"))
    }

    @Test
    fun testSplitForEmphasis_SingleWord() {
        assertEquals("" to "Settings", splitForEmphasis("Settings"))
    }

    @Test
    fun testSplitForEmphasis_TrimsAndCollapsesBoundaryWhitespace() {
        assertEquals("Text" to "count", splitForEmphasis("  Text   count  "))
    }

    @Test
    fun testSplitForEmphasis_NoSpacesScript() {
        assertEquals("" to "文本计数", splitForEmphasis("文本计数"))
    }

    @Test
    fun testSplitForEmphasis_Empty() {
        assertEquals("" to "", splitForEmphasis(""))
    }

    @Test
    fun testDetermineFontSizeForWordCount() {
        assertEquals(24.sp, determineFontSizeForWordCount("one two three"))
        assertEquals(20.sp, determineFontSizeForWordCount("one two three four"))
        assertEquals(16.sp, determineFontSizeForWordCount("1 2 3 4 5 6 7"))
        assertEquals(14.sp, determineFontSizeForWordCount("1 2 3 4 5 6 7 8 9 10 11"))
    }

    @Test
    fun testDetermineFontSizeForWordCount_EmptyAndBlank() {
        assertEquals(24.sp, determineFontSizeForWordCount(""))
        assertEquals(24.sp, determineFontSizeForWordCount("   \n\t "))
    }

    @Test
    fun testDetermineFontSizeForWordCount_TierBoundaries() {
        assertEquals(20.sp, determineFontSizeForWordCount("1 2 3 4 5 6"))
        assertEquals(16.sp, determineFontSizeForWordCount("1 2 3 4 5 6 7 8 9 10"))
    }

    @Test
    fun testCountWords_CollapsesMixedWhitespace() {
        assertEquals(3, countWords("  one\t\ttwo \n three  "))
    }

    @Test
    fun testCountWords_StopsAtLimit() {
        assertEquals(2, countWords("a b c d e", limit = 2))
    }

    @Test
    fun testCountWords_Empty() {
        assertEquals(0, countWords(""))
    }
}
