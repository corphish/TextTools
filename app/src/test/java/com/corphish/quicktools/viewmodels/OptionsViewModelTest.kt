package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.repository.ContextMenuOptionsRepository
import com.corphish.quicktools.repository.FeatureIds
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class OptionsViewModelTest {

    private lateinit var viewModel: OptionsViewModel
    private val contextMenuOptionsRepository: ContextMenuOptionsRepository = mockk()

    @Before
    fun setUp() {
        every { contextMenuOptionsRepository.getCurrentlyEnabledFeatures() } returns listOf(FeatureIds.EVAL, FeatureIds.TRANSFORM)
        viewModel = OptionsViewModel(contextMenuOptionsRepository)
    }

    @Test
    fun testEnabledFeatures() {
        assertEquals(2, viewModel.enabledFeatures.value.size)
        assertEquals(FeatureIds.EVAL, viewModel.enabledFeatures.value[0])
    }
}
