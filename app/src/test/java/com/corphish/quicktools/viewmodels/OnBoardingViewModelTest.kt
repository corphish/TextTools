package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.repository.ContextMenuOptionsRepository
import com.corphish.quicktools.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnBoardingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: OnBoardingViewModel
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val contextOptionsRepository: ContextMenuOptionsRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { settingsRepository.getOnboardingDone() } returns false
        viewModel = OnBoardingViewModel(settingsRepository, contextOptionsRepository)
    }

    @Test
    fun testSetOnBoardingDone() = runTest {
        viewModel.setOnBoardingDone(true)
        verify { settingsRepository.setOnboardingDone(true) }
        assertEquals(true, viewModel.onBoardingDone.value)
    }

    @Test
    fun testSetAppMode() = runTest {
        viewModel.setAppMode(AppMode.MULTI)
        verify { contextOptionsRepository.setCurrentAppMode(AppMode.MULTI) }
    }
}
