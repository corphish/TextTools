package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.usecases.WhatsappUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WUPViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: WUPViewModel
    private val whatsappUseCase: WhatsappUseCase = mockk()

    @Before
    fun setUp() {
        viewModel = WUPViewModel(whatsappUseCase)
    }

    @Test
    fun testDeterminePhoneNumber_Success() = runTest {
        val input = "1234567890"
        every { whatsappUseCase.determinePhoneNumber(input) } returns "1234567890"
        
        viewModel.determinePhoneNumber(input)
        
        assertTrue(viewModel.processedPhoneNumber.value is Result.Success)
        assertEquals("1234567890", (viewModel.processedPhoneNumber.value as Result.Success).value)
    }

    @Test
    fun testDeterminePhoneNumber_Error() = runTest {
        val input = "invalid"
        every { whatsappUseCase.determinePhoneNumber(input) } returns null
        
        viewModel.determinePhoneNumber(input)
        
        assertTrue(viewModel.processedPhoneNumber.value is Result.Error)
    }
}
