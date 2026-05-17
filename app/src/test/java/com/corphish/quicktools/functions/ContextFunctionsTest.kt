package com.corphish.quicktools.functions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class ContextFunctionsTest {

    private lateinit var contextFunctions: ContextFunctions
    private val context: Context = mockk(relaxed = true)
    private val clipboardManager: ClipboardManager = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { context.getSystemService(Context.CLIPBOARD_SERVICE) } returns clipboardManager
        contextFunctions = ContextFunctions(context)
        
        mockkStatic(ClipData::class)
        mockkStatic(Uri::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testCopyToClipboard() {
        val text = "hello"
        val clipData: ClipData = mockk()
        
        every { ClipData.newPlainText("text_tools_result", text) } returns clipData
        
        contextFunctions.copyToClipboard(text)
        
        verify { clipboardManager.setPrimaryClip(clipData) }
    }

    @Test
    fun testOpenInWeb() {
        val url = "https://example.com"
        val uri: Uri = mockk()
        
        every { Uri.parse(url) } returns uri
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().setAction(Intent.ACTION_VIEW) } returns mockk()
        every { anyConstructed<Intent>().setData(any()) } returns mockk()
        every { anyConstructed<Intent>().addFlags(any()) } returns mockk()
        
        contextFunctions.openInWeb(url)
        
        verify { context.startActivity(any()) }
    }
}
