package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.repository.ContextMenuOptionsRepository
import com.corphish.quicktools.repository.SettingsRepository
import com.corphish.quicktools.usecases.ManageTemplatesUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SettingsViewModel
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val contextMenuOptionsRepository: ContextMenuOptionsRepository = mockk(relaxed = true)
    private val manageTemplatesUseCase: ManageTemplatesUseCase = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { settingsRepository.getPrependCountryCodeEnabled() } returns false
        every { settingsRepository.getPrependCountryCode() } returns "+91"
        every { settingsRepository.getDecimalPoints() } returns 2
        every { settingsRepository.getEvaluateResultMode() } returns 0
        every { contextMenuOptionsRepository.getCurrentAppMode() } returns AppMode.SINGLE
        
        viewModel = SettingsViewModel(settingsRepository, contextMenuOptionsRepository, manageTemplatesUseCase)
    }

    @Test
    fun testUpdatePrependCountryCodeEnabled() = runTest {
        viewModel.updatePrependCountryCodeEnabled(true)
        verify { settingsRepository.setPrependCountryCodeEnabled(true) }
        assertEquals(true, viewModel.prependCountryCodeEnabled.value)
    }

    @Test
    fun testUpdatePrependCountryCode_Valid() = runTest {
        viewModel.updatePrependCountryCode("+1")
        verify { settingsRepository.setPrependCountryCode("+1") }
        assertEquals("+1", viewModel.prependCountryCode.value)
        assertTrue(viewModel.prependCountryCodeIsValid.value)
    }

    @Test
    fun testUpdatePrependCountryCode_Invalid() = runTest {
        viewModel.updatePrependCountryCode("1")
        verify(exactly = 0) { settingsRepository.setPrependCountryCode("1") }
        assertEquals("1", viewModel.prependCountryCode.value)
        assertFalse(viewModel.prependCountryCodeIsValid.value)
    }

    @Test
    fun testInvalidateCountryCodePrependSetting_Invalid() = runTest {
        viewModel.updatePrependCountryCode("1") // sets isValid to false
        viewModel.invalidateCountryCodePrependSetting()
        
        verify { settingsRepository.setPrependCountryCodeEnabled(false) }
        verify { settingsRepository.setPrependCountryCode("") }
    }

    @Test
    fun testUpdateDecimalPoints() = runTest {
        viewModel.updateDecimalPoints(4)
        verify { settingsRepository.setDecimalPoints(4) }
        assertEquals(4, viewModel.decimalPoints.value)
    }

    @Test
    fun testUpdateAppMode() = runTest {
        viewModel.updateAppMode(AppMode.MULTI)
        verify { contextMenuOptionsRepository.setCurrentAppMode(AppMode.MULTI) }
        assertEquals(AppMode.MULTI, viewModel.appMode.value)
    }

    @Test
    fun testClearAllTemplates() = runTest {
        viewModel.clearAllTemplates()
        verify { manageTemplatesUseCase.clearAllTemplates() }
    }
}
