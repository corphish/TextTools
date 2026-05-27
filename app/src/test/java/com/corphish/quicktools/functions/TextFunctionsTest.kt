package com.corphish.quicktools.functions

import androidx.compose.ui.text.TextRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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

    @Test
    fun testFindOccurrences() {
        val text = "hello world hello"
        
        // Match
        val res1 = textFunctions.findOccurrences(text, "hello", false)
        assertEquals(2, res1.size)
        assertEquals(TextRange(0, 5), res1[0])
        assertEquals(TextRange(12, 17), res1[1])

        // Case insensitive
        val res2 = textFunctions.findOccurrences(text, "HELLO", true)
        assertEquals(2, res2.size)

        // Case sensitive
        val res3 = textFunctions.findOccurrences(text, "HELLO", false)
        assertEquals(0, res3.size)

        // No match
        val res4 = textFunctions.findOccurrences(text, "hi", false)
        assertEquals(0, res4.size)

        // Empty search
        val res5 = textFunctions.findOccurrences(text, "", false)
        assertEquals(0, res5.size)
    }

    @Test
    fun testChangeCase() {
        val text = "hello world"
        assertEquals("HELLO WORLD", textFunctions.changeCase(text, 0))
        assertEquals("hello world", textFunctions.changeCase(text, 1))
        
        // titleCase(text, true) -> Capitalizes first word only
        assertEquals("Hello world", textFunctions.changeCase(text, 2))
        
        // titleCase(text, false) -> Capitalizes all words
        assertEquals("Hello World", textFunctions.changeCase(text, 3))
        
        // randomCase
        val randomCased = textFunctions.changeCase(text, 4)
        assertEquals(text.length, randomCased.length)
        assertEquals(text.lowercase(), randomCased.lowercase())
        
        // default case
        assertEquals(text, textFunctions.changeCase(text, 10))
    }

    @Test
    fun testTitleCaseMultiline() {
        val multilineText = "hello world\nhi there"
        
        // first word only - should capitalize first word of EACH line
        assertEquals("Hello world\nHi there", textFunctions.changeCase(multilineText, 2))
        
        // all words - should capitalize all words of EACH line
        assertEquals("Hello World\nHi There", textFunctions.changeCase(multilineText, 3))
    }

    @Test
    fun testCustomWrap() {
        val text = "text"
        // even length wrap - splits in middle
        assertEquals("((text))", textFunctions.customWrap(text, "(())"))
        assertEquals("abtextcd", textFunctions.customWrap(text, "abcd"))
        assertEquals("atextb", textFunctions.customWrap(text, "ab"))
        
        // odd length wrap - wraps with full string on both sides
        assertEquals("***text***", textFunctions.customWrap(text, "***"))
        assertEquals("atexta", textFunctions.customWrap(text, "a"))
    }

    @Test
    fun testPresetWrap() {
        val text = "hello"
        assertEquals("'hello'", textFunctions.presetWrap(text, 0))
        assertEquals("\"hello\"", textFunctions.presetWrap(text, 1))
        assertEquals("(hello)", textFunctions.presetWrap(text, 2))
        assertEquals("{hello}", textFunctions.presetWrap(text, 3))
        assertEquals("[hello]", textFunctions.presetWrap(text, 4))
        assertEquals("hello", textFunctions.presetWrap(text, 5))
    }

    @Test
    fun testSortLines() {
        assertEquals("a\nb\nc", textFunctions.sortLines("c\na\nb"))
        assertEquals("1\n2\n3", textFunctions.sortLines("3\n1\n2"))
        assertEquals("", textFunctions.sortLines(""))
    }

    @Test
    fun testRepeatText() {
        assertEquals("abcabcabc", textFunctions.repeatText("abc", 3))
        assertEquals("", textFunctions.repeatText("abc", 0))
        assertEquals("", textFunctions.repeatText("", 5))
    }

    @Test
    fun testRemoveText() {
        val text = "hello world hello"
        // Mode 0: remove first
        assertEquals(" world hello", textFunctions.removeText(text, "hello", 0))
        
        // Mode 1: remove last
        // Note: The implementation of removeText mode 1 is:
        // val reversed = reverseText(text)
        // val replaced = reversed.replaceFirst(remove, "")
        // reverseText(replaced)
        // So for "hello world hello", removing "hello" mode 1:
        // reversed = "olleh dlrow olleh"
        // replaced = " dlrow olleh" (removes first "olleh")
        // reverseText = "hello world "
        assertEquals("hello world ", textFunctions.removeText(text, "olleh", 1))
        
        // Mode 2: remove all
        assertEquals(" world ", textFunctions.removeText(text, "hello", 2))
    }

    @Test
    fun testReverseText() {
        assertEquals("olleh", textFunctions.reverseText("hello"))
        assertEquals("12345", textFunctions.reverseText("54321"))
        assertEquals("", textFunctions.reverseText(""))
    }

    @Test
    fun testNumberLines() {
        assertEquals("1. line1\n2. line2", textFunctions.numberLines("line1\nline2"))
    }

    @Test
    fun testAddPrefixSuffix() {
        assertEquals("prehello", textFunctions.addPrefix("hello", "pre"))
        assertEquals("hellosuf", textFunctions.addSuffix("hello", "suf"))
    }

    @Test
    fun testReverseWords() {
        assertEquals("olleh dlrow", textFunctions.reverseWords("hello world"))
        assertEquals("a b c", textFunctions.reverseWords("a b c"))
    }

    @Test
    fun testReverseLines() {
        assertEquals("olleh\ndlrow", textFunctions.reverseLines("hello\nworld"))
    }

    @Test
    fun testRemoveWhiteSpaces() {
        assertEquals("helloworld", textFunctions.removeWhiteSpaces("hello world"))
        assertEquals("abc", textFunctions.removeWhiteSpaces(" a b c "))
    }

    @Test
    fun testRemoveLineBreaks() {
        assertEquals("helloworld", textFunctions.removeLineBreaks("hello\nworld"))
    }

    @Test
    fun testRemoveEmptyLines() {
        assertEquals("a\nb", textFunctions.removeEmptyLines("a\n\nb"))
        assertEquals("a\nb", textFunctions.removeEmptyLines("\na\n\nb\n"))
    }

    @Test
    fun testRemoveDuplicateWords() {
        val text = "hello hello world Hello"
        // ignoreCase = true
        assertEquals("hello world", textFunctions.removeDuplicateWords(text, true))
        // ignoreCase = false
        assertEquals("hello world Hello", textFunctions.removeDuplicateWords(text, false))
    }

    @Test
    fun testRemoveDuplicateLines() {
        val text = "line1\nline1\nline2\nLine1"
        // ignoreCase = true
        assertEquals("line1\nline2", textFunctions.removeDuplicateLines(text, true))
        // ignoreCase = false
        assertEquals("line1\nline2\nLine1", textFunctions.removeDuplicateLines(text, false))
    }

    @Test
    fun testUnicodeTransforms() {
        val text = "Abc 123"
        // Basic sanity checks: output should be different from input and not empty
        assertNotEquals(text, textFunctions.boldSerif(text))
        assertNotEquals(text, textFunctions.italicSerif(text))
        assertNotEquals(text, textFunctions.boldItalicSerif(text))
        assertNotEquals(text, textFunctions.boldSans(text))
        assertNotEquals(text, textFunctions.italicSans(text))
        assertNotEquals(text, textFunctions.boldItalicSans(text))
        assertNotEquals(text, textFunctions.shortStrikethrough(text))
        assertNotEquals(text, textFunctions.longStrikethrough(text))
        assertNotEquals(text, textFunctions.cursive(text))
        assertNotEquals(text, textFunctions.monospaceFont(text))
    }

    @Test
    fun testLineBreaks() {
        val text = "abcdef"
        assertEquals("abc\ndef", textFunctions.lineBreakByCharacter(text, 3))
        assertEquals(text, textFunctions.lineBreakByCharacter(text, 0))

        val words = "one two three"
        // Current implementation adds space before newline: "one two \nthree"
        assertEquals("one two \nthree", textFunctions.lineBreakByWords(words, 2))
    }

    @Test
    fun testSqueeze() {
        val text = "abc\ndef"
        // maxCharsPerLine = 10
        assertEquals("abcdef", textFunctions.squeeze(text, 10))
        // maxCharsPerLine = 2
        assertEquals("ab\ncd\nef", textFunctions.squeeze(text, 2))
    }

    @Test
    fun testPrependAppendLines() {
        val text = "a\nb"
        assertEquals(">a\n>b", textFunctions.prependLines(text, ">"))
        assertEquals("a<\nb<", textFunctions.appendLines(text, "<"))
    }
}
