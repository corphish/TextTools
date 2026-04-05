package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.repository.SettingsRepository
import com.corphish.quicktools.usecases.WhatsappUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WUPViewModel @Inject constructor(
    private val whatsappUseCase: WhatsappUseCase
): ViewModel() {
    // Nullable type to denote when phone number is invalid
    private val _processedPhoneNumber = MutableStateFlow<Result<String>>(Result.Initial)
    val processedPhoneNumber: StateFlow<Result<String>> = _processedPhoneNumber

    fun determinePhoneNumber(data: String?) {
        viewModelScope.launch {
            val number = whatsappUseCase.determinePhoneNumber(data)
            _processedPhoneNumber.emit(
                if (number == null) Result.Error else Result.Success(number)
            )
        }
    }
}