package com.corphish.quicktools.viewmodels

import android.net.Uri
import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.usecases.SaveTextUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveTextViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val saveTextUseCase: SaveTextUseCase = mockk()
    private lateinit var viewModel: SaveTextViewModel

    @Before
    fun setUp() {
        viewModel = SaveTextViewModel(saveTextUseCase)
    }

    @Test
    fun testSaveText_Success() = runTest {
        val uri: Uri = mockk()
        val uriString = "content://test"
        every { uri.toString() } returns uriString
        
        val text = "hello world"
        every { saveTextUseCase.execute(uriString, text) } returns true
        
        viewModel.saveText(uri, text)
        
        val status = viewModel.saveTextStatus.value
        assertTrue(status is Result.Success)
    }

    @Test
    fun testSaveText_Error() = runTest {
        val uri: Uri = mockk()
        val uriString = "content://test"
        every { uri.toString() } returns uriString
        
        val text = "hello world"
        every { saveTextUseCase.execute(uriString, text) } returns false
        
        viewModel.saveText(uri, text)
        
        val status = viewModel.saveTextStatus.value
        assertTrue(status is Result.Error)
    }
}
