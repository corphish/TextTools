package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.repository.TextTemplateRepository
import javax.inject.Inject

class ManageTemplatesUseCase @Inject constructor(
    private val textTemplateRepository: TextTemplateRepository
) {
    fun getTemplates() = textTemplateRepository.getTemplates()
    fun saveTemplate(template: TextTemplate) = textTemplateRepository.saveTemplate(template)
    fun deleteTemplate(id: String) = textTemplateRepository.deleteTemplate(id)
    fun updateTemplate(template: TextTemplate) = textTemplateRepository.updateTemplate(template)
    fun clearAllTemplates() = textTemplateRepository.clearAllTemplates()
}
