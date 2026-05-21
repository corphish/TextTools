package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.data.TextCountResult
import com.corphish.quicktools.usecases.TextCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextCountViewModel @Inject constructor(
    private val textCountUseCase: TextCountUseCase
) : ViewModel() {
    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val _countResult = MutableStateFlow(TextCountResult())
    val countResult = _countResult.asStateFlow()

    fun setTextAndProcess(text: String) {
        viewModelScope.launch {
            _text.value = text
            _countResult.value = textCountUseCase.execute(text)
        }
    }
}
