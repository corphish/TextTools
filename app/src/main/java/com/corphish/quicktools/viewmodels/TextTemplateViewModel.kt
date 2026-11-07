package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.data.TemplateType
import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.data.Walkthrough
import com.corphish.quicktools.data.Walkthroughs
import com.corphish.quicktools.usecases.ApplyTemplateUseCase
import com.corphish.quicktools.usecases.ManageTemplatesUseCase
import com.corphish.quicktools.usecases.WalkthroughUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TextTemplateViewModel @Inject constructor(
    private val manageTemplatesUseCase: ManageTemplatesUseCase,
    private val applyTemplateUseCase: ApplyTemplateUseCase,
    private val walkthroughUseCase: WalkthroughUseCase,
) : ViewModel() {

    private val _templates = MutableStateFlow<List<TextTemplate>>(emptyList())
    val templates: StateFlow<List<TextTemplate>> = _templates.asStateFlow()

    private val _templatesLoaded = MutableStateFlow(false)

    /**
     * Walkthroughs of the screen while managing templates; empty until the templates are loaded.
     * - Seen [Walkthroughs.TEMPLATE_MANAGE]: none.
     * - Seen [Walkthroughs.TEMPLATE_CREATE]: [Walkthroughs.TEMPLATE_EDIT], which starts once a
     *   template exists.
     * - Templates exist: [Walkthroughs.TEMPLATE_MANAGE], covering creating and editing at once.
     * - No templates: [Walkthroughs.TEMPLATE_CREATE] now, and [Walkthroughs.TEMPLATE_EDIT] once
     *   the first template is created.
     */
    val manageWalkthroughs: StateFlow<List<Walkthrough>> = combine(_templates, _templatesLoaded) { templates, loaded ->
        when {
            !loaded -> emptyList()
            !walkthroughUseCase.isPending(Walkthroughs.TEMPLATE_MANAGE) -> emptyList()
            !walkthroughUseCase.isPending(Walkthroughs.TEMPLATE_CREATE) -> listOf(Walkthroughs.TEMPLATE_EDIT)
            templates.isNotEmpty() -> listOf(Walkthroughs.TEMPLATE_MANAGE)
            else -> listOf(Walkthroughs.TEMPLATE_CREATE, Walkthroughs.TEMPLATE_EDIT)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _userInput = MutableStateFlow("")

    /** Text the templates are applied to: the selected text in selection mode. */
    val userInput: StateFlow<String> = _userInput.asStateFlow()

    /** Result of applying each template to [userInput], keyed by template id. */
    val previews: StateFlow<Map<String, String>> = combine(_templates, _userInput) { templates, input ->
        templates.associate { it.id to render(it.template, input, it.type) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    fun loadTemplates() {
        viewModelScope.launch {
            _templates.value = manageTemplatesUseCase.getTemplates()
            _templatesLoaded.value = true
        }
    }

    /** Sets the text the templates are applied to. */
    fun setUserInput(input: String) {
        _userInput.value = input
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

    /** Applies [template] to [userInput]. */
    fun applyTemplate(template: TextTemplate): String =
        render(template.template, _userInput.value, template.type)

    /**
     * Applies a template value to any text, e.g. for an example of how templates work.
     *
     * @param template Template value, with `%s` where [input] goes.
     * @param input Text to insert.
     * @param type How [input] is processed before insertion.
     * @return The resulting text.
     */
    fun render(template: String, input: String, type: TemplateType): String =
        applyTemplateUseCase.execute(template, input, type)
}
