package com.corphish.quicktools.repository

import com.corphish.quicktools.functions.FileFunctions
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TextRepositoryImplTest {

    private lateinit var repository: TextRepositoryImpl
    private val fileFunctions: FileFunctions = mockk()

    @Before
    fun setUp() {
        repository = TextRepositoryImpl(fileFunctions)
    }

    @Test
    fun testWriteText_Success() {
        val uriString = "content://test"
        val text = "hello world"
        every { fileFunctions.saveTextToUri(uriString, text) } returns true
        
        val result = repository.writeText(uriString, text)
        
        assertTrue(result)
    }

    @Test
    fun testWriteText_Failure() {
        val uriString = "content://test"
        val text = "hello world"
        every { fileFunctions.saveTextToUri(uriString, text) } returns false
        
        val result = repository.writeText(uriString, text)
        
        assertFalse(result)
    }
}
