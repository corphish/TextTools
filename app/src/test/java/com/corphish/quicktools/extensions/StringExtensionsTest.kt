package com.corphish.quicktools.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun testTruncate() {
        val shortString = "hello"
        assertEquals("hello", shortString.truncate(10))
        
        val longString = "this is a long string"
        // default len is 16
        // "this is a lon..." -> length 16
        assertEquals("this is a lon...", longString.truncate())
        
        assertEquals("this...", longString.truncate(7))
        assertEquals("t...", longString.truncate(4))
        
        // string exactly at length
        val exactString = "1234567890123456"
        assertEquals(exactString, exactString.truncate(16))
    }
}
