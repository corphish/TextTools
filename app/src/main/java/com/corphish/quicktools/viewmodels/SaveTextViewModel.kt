package com.corphish.quicktools.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.repository.TextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveTextViewModel @Inject constructor(private val textRepository: TextRepository) :
    ViewModel() {
    private val _saveTextStatus = MutableStateFlow<Result<Boolean>>(Result.Initial)
    val saveTextStatus: StateFlow<Result<Boolean>> = _saveTextStatus

    fun saveText(uri: Uri?, text: String) {
        viewModelScope.launch {
            val res = textRepository.writeText(uri, text)
            _saveTextStatus.value = if (res) Result.Success(true) else Result.Error
        }
    }
}