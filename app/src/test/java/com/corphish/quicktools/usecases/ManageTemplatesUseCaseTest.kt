package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.repository.TextTemplateRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ManageTemplatesUseCaseTest {
    private lateinit var useCase: ManageTemplatesUseCase
    private val repository: TextTemplateRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        useCase = ManageTemplatesUseCase(repository)
    }

    @Test
    fun testGetTemplates() {
        useCase.getTemplates()
        verify { repository.getTemplates() }
    }

    @Test
    fun testSaveTemplate() {
        val template = TextTemplate("1", "T1", "V1")
        useCase.saveTemplate(template)
        verify { repository.saveTemplate(template) }
    }

    @Test
    fun testDeleteTemplate() {
        useCase.deleteTemplate("1")
        verify { repository.deleteTemplate("1") }
    }

    @Test
    fun testUpdateTemplate() {
        val template = TextTemplate("1", "T1", "V1")
        useCase.updateTemplate(template)
        verify { repository.updateTemplate(template) }
    }

    @Test
    fun testClearAllTemplates() {
        useCase.clearAllTemplates()
        verify { repository.clearAllTemplates() }
    }
}
