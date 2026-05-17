package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.usecases.ClipboardUseCase
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextTransformViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TextTransformViewModel
    private val clipboardUseCase: ClipboardUseCase = mockk(relaxed = true)

    @Before
    fun setUp() {
        viewModel = TextTransformViewModel(clipboardUseCase)
    }

    @Test
    fun testInitializeText() = runTest {
        viewModel.initializeText("hello")
        assertEquals("hello", viewModel.mainText.value)
        assertEquals("hello", viewModel.previewText.value)
    }

    @Test
    fun testTransform_ChangeCase() = runTest {
        viewModel.initializeText("hello")
        viewModel.selectPrimaryIndex(TextTransformViewModel.INDEX_CHANGE_CASE)
        viewModel.selectSecondaryIndex(0) // Uppercase
        
        assertEquals("HELLO", viewModel.previewText.value)
    }

    @Test
    fun testTransform_WrapText() = runTest {
        viewModel.initializeText("hello")
        viewModel.selectPrimaryIndex(TextTransformViewModel.INDEX_WRAP_TEXT)
        viewModel.selectSecondaryIndex(0) // Single inverted comma
        
        assertEquals("'hello'", viewModel.previewText.value)
    }

    @Test
    fun testTransform_RepeatText() = runTest {
        viewModel.initializeText("abc")
        viewModel.selectPrimaryIndex(TextTransformViewModel.INDEX_REPEAT_TEXT)
        viewModel.setSecondaryText("3")
        
        assertEquals("abcabcabc", viewModel.previewText.value)
    }

    @Test
    fun testTransform_AddPrefix() = runTest {
        viewModel.initializeText("world")
        viewModel.selectPrimaryIndex(TextTransformViewModel.INDEX_ADD_PREFIX_SUFFIX)
        viewModel.selectSecondaryIndex(0) // Prefix
        viewModel.setSecondaryText("hello ")
        
        assertEquals("hello world", viewModel.previewText.value)
    }
    
    @Test
    fun testTransform_RemoveText() = runTest {
        viewModel.initializeText("hello world")
        viewModel.selectPrimaryIndex(TextTransformViewModel.INDEX_REMOVE_TEXT)
        viewModel.selectSecondaryIndex(0) // Remove first
        viewModel.setSecondaryText("hello ")
        
        assertEquals("world", viewModel.previewText.value)
    }
}
