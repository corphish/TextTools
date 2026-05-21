package com.corphish.quicktools.functions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextFunctionsTest {

    private val textFunctions = TextFunctions()

    @Test
    fun testCountText_Basic() {
        val text = "Hello world 123!"
        val result = textFunctions.countText(text)
        
        assertEquals(16, result.characterCount)
        assertEquals(10, result.letterCount) // "Hello world"
        assertEquals(3, result.digitCount)   // "123"
        assertEquals(2, result.spaceCount)   // 2 spaces
        assertEquals(1, result.symbolCount)  // "!"
        assertEquals(3, result.wordCount)    // "Hello", "world", "123!"
    }

    @Test
    fun testCountText_WordFrequency() {
        val text = "apple banana apple orange banana apple"
        val result = textFunctions.countText(text)
        
        val freq = result.wordFrequency
        assertEquals(3, freq.size)
        assertEquals("apple" to 3, freq[0])
        assertEquals("banana" to 2, freq[1])
        assertEquals("orange" to 1, freq[2])
    }

    @Test
    fun testCountText_Empty() {
        val result = textFunctions.countText("")
        assertEquals(0, result.characterCount)
        assertEquals(0, result.wordCount)
        assertTrue(result.wordFrequency.isEmpty())
    }

    @Test
    fun testCountText_OnlySpaces() {
        val result = textFunctions.countText("   ")
        assertEquals(3, result.characterCount)
        assertEquals(3, result.spaceCount)
        assertEquals(0, result.wordCount)
        assertTrue(result.wordFrequency.isEmpty())
    }
}
