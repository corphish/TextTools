package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.repository.ContextMenuOptionsRepository
import com.corphish.quicktools.repository.FeatureIds
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MainViewModel
    private val contextMenuOptionsRepository: ContextMenuOptionsRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        viewModel = MainViewModel(contextMenuOptionsRepository)
    }

    @Test
    fun testInit() {
        every { contextMenuOptionsRepository.getCurrentlyEnabledFeatures() } returns listOf(FeatureIds.EVAL)
        every { contextMenuOptionsRepository.getCurrentAppMode() } returns AppMode.SINGLE
        
        viewModel.init()
        
        assertEquals(listOf(FeatureIds.EVAL), viewModel.enabledFeatures.value)
        assertEquals(AppMode.SINGLE, viewModel.appMode.value)
    }

    @Test
    fun testEnableOrDisableFeature() = runTest {
        every { contextMenuOptionsRepository.getCurrentlyEnabledFeatures() } returns listOf(FeatureIds.EVAL)
        
        viewModel.enableOrDisableFeature(FeatureIds.EVAL, true)
        
        coEvery { contextMenuOptionsRepository.enableOrDisableFeature(FeatureIds.EVAL, true) }
        assertEquals(listOf(FeatureIds.EVAL), viewModel.enabledFeatures.value)
    }
}
