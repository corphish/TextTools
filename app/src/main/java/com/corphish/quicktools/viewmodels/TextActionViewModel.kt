package com.corphish.quicktools.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.functions.ParsedTextAction
import com.corphish.quicktools.usecases.TextActionDeterminationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextActionViewModel @Inject constructor(
    private val textActionDeterminationUseCase: TextActionDeterminationUseCase
): ViewModel() {

    // Actions
    private val _textActionsFlow = MutableStateFlow<TexActionState>(TexActionState.Loading)
    val textActionsFlow = _textActionsFlow.asStateFlow()

    fun determineActions(text: String, canBeApplied: Boolean) {
        viewModelScope.launch {
            _textActionsFlow.value = TexActionState.Loading
            _textActionsFlow.value = TexActionState.Result(textActionDeterminationUseCase.buildTextActions(text, canBeApplied))
        }
    }

    fun performAction(action: ParsedTextAction) =
        textActionDeterminationUseCase.performAction(action)
}

sealed class TexActionState {
    data class Result(val actions: List<ParsedTextAction>): TexActionState()
    object Loading: TexActionState()
}