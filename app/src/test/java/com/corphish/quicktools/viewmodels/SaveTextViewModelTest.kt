package com.corphish.quicktools.viewmodels

import android.net.Uri
import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.repository.TextRepository
import io.mockk.coEvery
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

    private lateinit var viewModel: SaveTextViewModel
    private val textRepository: TextRepository = mockk()

    @Before
    fun setUp() {
        viewModel = SaveTextViewModel(textRepository)
    }

    @Test
    fun testSaveText_Success() = runTest {
        val uri: Uri = mockk()
        val text = "some text"
        coEvery { textRepository.writeText(uri, text) } returns true
        
        viewModel.saveText(uri, text)
        
        assertTrue(viewModel.saveTextStatus.value is Result.Success)
    }

    @Test
    fun testSaveText_Failure() = runTest {
        val uri: Uri = mockk()
        val text = "some text"
        coEvery { textRepository.writeText(uri, text) } returns false
        
        viewModel.saveText(uri, text)
        
        assertTrue(viewModel.saveTextStatus.value is Result.Error)
    }
}
