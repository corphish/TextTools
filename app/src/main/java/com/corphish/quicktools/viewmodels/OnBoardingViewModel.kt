package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.repository.ContextMenuOptionsRepository
import com.corphish.quicktools.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val contextOptionsRepository: ContextMenuOptionsRepository,
) : ViewModel() {
    private val _onBoardingDone = MutableStateFlow(settingsRepository.getOnboardingDone())
    val onBoardingDone = _onBoardingDone.asStateFlow()

    private val _appMode = MutableStateFlow(contextOptionsRepository.getCurrentAppMode())
    val appMode = _appMode.asStateFlow()

    fun setOnBoardingDone(done: Boolean) {
        viewModelScope.launch {
            settingsRepository.setOnboardingDone(done)
            _onBoardingDone.value = done
        }
    }

    fun setAppMode(mode: AppMode) {
        viewModelScope.launch {
            contextOptionsRepository.setCurrentAppMode(mode)
            _appMode.value = mode
        }
    }
}