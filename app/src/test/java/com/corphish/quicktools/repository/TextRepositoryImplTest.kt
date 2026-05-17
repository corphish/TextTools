package com.corphish.quicktools.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.IOException

class TextRepositoryImplTest {

    private lateinit var repository: TextRepositoryImpl
    private val context: Context = mockk()
    private val contentResolver: ContentResolver = mockk()
    private val uri: Uri = mockk()

    @Before
    fun setUp() {
        every { context.contentResolver } returns contentResolver
        repository = TextRepositoryImpl(context)
    }

    @Test
    fun testWriteText_Success() {
        val outputStream = ByteArrayOutputStream()
        every { contentResolver.openOutputStream(uri) } returns outputStream
        
        val text = "hello world"
        val result = repository.writeText(uri, text)
        
        assertTrue(result)
        assertEquals(text, outputStream.toString())
    }

    @Test
    fun testWriteText_NullUri() {
        val result = repository.writeText(null, "text")
        assertFalse(result)
    }

    @Test
    fun testWriteText_IOException() {
        every { contentResolver.openOutputStream(uri) } throws IOException("error")
        
        val result = repository.writeText(uri, "text")
        
        assertFalse(result)
    }
}
