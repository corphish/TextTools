package com.corphish.quicktools.repository

import com.corphish.quicktools.functions.TextFunctions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TextTransformRepositoryTest {

    private lateinit var repository: TextTransformRepositoryImpl
    private val textFunctions: TextFunctions = mockk()

    @Before
    fun setUp() {
        repository = TextTransformRepositoryImpl(textFunctions)
    }

    @Test
    fun testTransform_None() {
        val result = repository.transform("hello", TextTransformRepositoryImpl.INDEX_NONE, 0, "")
        assertEquals("hello", result)
    }

    @Test
    fun testTransform_WrapText_Preset() {
        every { textFunctions.presetWrap("hello", 2) } returns "(hello)"
        
        val result = repository.transform("hello", TextTransformRepositoryImpl.INDEX_WRAP_TEXT, 2, "")
        
        assertEquals("(hello)", result)
        verify { textFunctions.presetWrap("hello", 2) }
    }

    @Test
    fun testTransform_WrapText_Custom() {
        every { textFunctions.customWrap("hello", "**") } returns "**hello**"
        
        val result = repository.transform("hello", TextTransformRepositoryImpl.INDEX_WRAP_TEXT, 5, "**")
        
        assertEquals("**hello**", result)
        verify { textFunctions.customWrap("hello", "**") }
    }

    @Test
    fun testTransform_ChangeCase() {
        every { textFunctions.changeCase("hello", 0) } returns "HELLO"
        
        val result = repository.transform("hello", TextTransformRepositoryImpl.INDEX_CHANGE_CASE, 0, "")
        
        assertEquals("HELLO", result)
        verify { textFunctions.changeCase("hello", 0) }
    }

    @Test
    fun testTransform_RepeatText() {
        every { textFunctions.repeatText("abc", 3) } returns "abcabcabc"
        
        val result = repository.transform("abc", TextTransformRepositoryImpl.INDEX_REPEAT_TEXT, 0, "3")
        
        assertEquals("abcabcabc", result)
        verify { textFunctions.repeatText("abc", 3) }
    }

    @Test
    fun testTransform_RemoveText() {
        every { textFunctions.removeText("hello", "l", 0) } returns "helo"
        
        val result = repository.transform("hello", TextTransformRepositoryImpl.INDEX_REMOVE_TEXT, 0, "l")
        
        assertEquals("helo", result)
        verify { textFunctions.removeText("hello", "l", 0) }
    }

    @Test
    fun testTransform_RemoveWhiteSpaces() {
        every { textFunctions.removeWhiteSpaces("a b c") } returns "abc"
        
        val result = repository.transform("a b c", TextTransformRepositoryImpl.INDEX_REMOVE_TEXT, 3, "")
        
        assertEquals("abc", result)
        verify { textFunctions.removeWhiteSpaces("a b c") }
    }

    @Test
    fun testTransform_DecorateText() {
        every { textFunctions.clearUnicodeFormatting("hello") } returns "hello"
        every { textFunctions.boldSerif("hello") } returns "𝐛𝐞𝐥𝐥𝐨"
        
        val result = repository.transform("hello", TextTransformRepositoryImpl.INDEX_DECORATE_TEXT, 0, "")
        
        assertEquals("𝐛𝐞𝐥𝐥𝐨", result)
        verify { 
            textFunctions.clearUnicodeFormatting("hello")
            textFunctions.boldSerif("hello")
        }
    }
}
