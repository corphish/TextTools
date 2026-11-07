package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.data.TemplateType
import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.usecases.ApplyTemplateUseCase
import com.corphish.quicktools.usecases.ManageTemplatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TextTemplateViewModel @Inject constructor(
    private val manageTemplatesUseCase: ManageTemplatesUseCase,
    private val applyTemplateUseCase: ApplyTemplateUseCase
) : ViewModel() {

    private val _templates = MutableStateFlow<List<TextTemplate>>(emptyList())
    val templates: StateFlow<List<TextTemplate>> = _templates.asStateFlow()

    /** Text the templates are applied to; only read when applying, so it is not observable. */
    private var userInput = ""

    fun loadTemplates() {
        viewModelScope.launch {
            _templates.value = manageTemplatesUseCase.getTemplates()
        }
    }

    /** Sets the text the templates are applied to. */
    fun setUserInput(input: String) {
        userInput = input
    }

    fun addTemplate(name: String, template: String, type: TemplateType) {
        val newTemplate = TextTemplate(
            id = UUID.randomUUID().toString(),
            name = name,
            template = template,
            type = type
        )
        manageTemplatesUseCase.saveTemplate(newTemplate)
        loadTemplates()
    }

    fun updateTemplate(id: String, name: String, template: String, type: TemplateType) {
        val updatedTemplate = TextTemplate(id = id, name = name, template = template, type = type)
        manageTemplatesUseCase.updateTemplate(updatedTemplate)
        loadTemplates()
    }

    fun deleteTemplate(id: String) {
        manageTemplatesUseCase.deleteTemplate(id)
        loadTemplates()
    }

    fun applyTemplate(template: TextTemplate): String {
        return applyTemplateUseCase.execute(template.template, userInput, template.type)
    }
}
