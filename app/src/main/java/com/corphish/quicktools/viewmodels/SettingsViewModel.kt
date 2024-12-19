package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.BuildConfig
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.repository.ContextMenuOptionsRepository
import com.corphish.quicktools.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val contextMenuOptionsRepository: ContextMenuOptionsRepository,
): ViewModel() {
    private val _prependCountryCodeEnabled = MutableStateFlow(settingsRepository.getPrependCountryCodeEnabled())
    private val _prependCountryCode = MutableStateFlow(settingsRepository.getPrependCountryCode())
    private val _decimalPoints = MutableStateFlow(settingsRepository.getDecimalPoints())
    private val _evalResultMode = MutableStateFlow(settingsRepository.getEvaluateResultMode())
    private val _appVersionName = MutableStateFlow(BuildConfig.VERSION_NAME)
    private val _appVersionCode = MutableStateFlow(BuildConfig.VERSION_CODE)
    private val _appMode = MutableStateFlow(contextMenuOptionsRepository.getCurrentAppMode())

    val prependCountryCodeEnabled: StateFlow<Boolean> = _prependCountryCodeEnabled
    val prependCountryCode: StateFlow<String?> = _prependCountryCode
    val decimalPoints: StateFlow<Int> = _decimalPoints
    val evalResultMode: StateFlow<Int> = _evalResultMode
    val appVersionName: StateFlow<String> = _appVersionName
    val appVersionCode: StateFlow<Int> = _appVersionCode
    val appMode: StateFlow<AppMode> = _appMode

    fun updatePrependCountryCodeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setPrependCountryCodeEnabled(enabled)
            _prependCountryCodeEnabled.value = enabled
        }
    }

    fun updatePrependCountryCode(code: String) {
        viewModelScope.launch {
            settingsRepository.setPrependCountryCode(code)
            _prependCountryCode.value = code
        }
    }

    fun updateDecimalPoints(count: Int) {
        viewModelScope.launch {
            settingsRepository.setDecimalPoints(count)
            _decimalPoints.value = count
        }
    }

    fun updateEvaluateResultMode(mode: Int) {
        viewModelScope.launch {
            settingsRepository.setEvaluateResultMode(mode)
            _evalResultMode.value = mode
        }
    }

    fun updateAppMode(mode: AppMode) {
        viewModelScope.launch {
            contextMenuOptionsRepository.setCurrentAppMode(mode)
            _appMode.value = mode
        }
    }
}