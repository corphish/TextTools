package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.usecases.ApplyTemplateUseCase
import com.corphish.quicktools.usecases.ManageTemplatesUseCase
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
class TextTemplateViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TextTemplateViewModel
    private val manageTemplatesUseCase: ManageTemplatesUseCase = mockk(relaxed = true)
    private val applyTemplateUseCase: ApplyTemplateUseCase = mockk(relaxed = true)

    @Before
    fun setUp() {
        viewModel = TextTemplateViewModel(manageTemplatesUseCase, applyTemplateUseCase)
    }

    @Test
    fun testLoadTemplates() = runTest {
        val templates = listOf(TextTemplate("1", "Template 1", "Hello %s"))
        every { manageTemplatesUseCase.getTemplates() } returns templates

        viewModel.loadTemplates()

        assertEquals(templates, viewModel.templates.value)
    }

    @Test
    fun testSetUserInput() = runTest {
        viewModel.setUserInput("World")
        assertEquals("World", viewModel.userInput.value)
    }

    @Test
    fun testAddTemplate() = runTest {
        viewModel.addTemplate("New Template", "Welcome %s")
        verify { manageTemplatesUseCase.saveTemplate(any()) }
        verify { manageTemplatesUseCase.getTemplates() }
    }

    @Test
    fun testUpdateTemplate() = runTest {
        viewModel.updateTemplate("1", "Updated Name", "Updated Template %s")
        verify { manageTemplatesUseCase.updateTemplate(any()) }
        verify { manageTemplatesUseCase.getTemplates() }
    }

    @Test
    fun testDeleteTemplate() = runTest {
        viewModel.deleteTemplate("1")
        verify { manageTemplatesUseCase.deleteTemplate("1") }
        verify { manageTemplatesUseCase.getTemplates() }
    }

    @Test
    fun testApplyTemplate() = runTest {
        viewModel.setUserInput("Android")
        every { applyTemplateUseCase.execute("Hello %s", "Android") } returns "Hello Android"

        val result = viewModel.applyTemplate("Hello %s")

        assertEquals("Hello Android", result)
    }
}
