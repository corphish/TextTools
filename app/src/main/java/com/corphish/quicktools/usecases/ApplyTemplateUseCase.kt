package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.TemplateType
import com.corphish.quicktools.repository.TextTemplateRepository
import javax.inject.Inject

class ApplyTemplateUseCase @Inject constructor(
    private val textTemplateRepository: TextTemplateRepository
) {
    fun execute(template: String, input: String, type: TemplateType): String {
        return textTemplateRepository.applyTemplate(template, input, type)
    }
}
