package com.corphish.quicktools.functions

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.IOException

class FileFunctionsTest {

    private lateinit var fileFunctions: FileFunctions
    private val context: Context = mockk()
    private val contentResolver: ContentResolver = mockk()

    @Before
    fun setUp() {
        every { context.contentResolver } returns contentResolver
        fileFunctions = FileFunctions(context)
        
        // Mocking Uri.parse as it's an Android class
        mockkStatic(Uri::class)
    }

    @Test
    fun testSaveTextToUri_Success() {
        val uriString = "content://test"
        val text = "hello world"
        val uri: Uri = mockk()
        val outputStream = ByteArrayOutputStream()

        every { Uri.parse(uriString) } returns uri
        every { contentResolver.openOutputStream(uri) } returns outputStream

        val result = fileFunctions.saveTextToUri(uriString, text)

        assertTrue(result)
        assertEquals(text, outputStream.toString())
    }

    @Test
    fun testSaveTextToUri_EmptyUri() {
        val result = fileFunctions.saveTextToUri("", "text")
        assertFalse(result)
    }

    @Test
    fun testSaveTextToUri_IOException() {
        val uriString = "content://test"
        val uri: Uri = mockk()
        every { Uri.parse(uriString) } returns uri
        every { contentResolver.openOutputStream(uri) } throws IOException("error")

        val result = fileFunctions.saveTextToUri(uriString, "text")

        assertFalse(result)
    }
}
