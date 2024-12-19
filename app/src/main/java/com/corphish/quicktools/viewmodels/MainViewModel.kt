package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.repository.ContextMenuOptionsRepository
import com.corphish.quicktools.repository.FeatureIds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val contextMenuOptionsRepository: ContextMenuOptionsRepository) :
    ViewModel() {
    private val _enabledFeatures =
        MutableStateFlow(listOf<FeatureIds>())
    val enabledFeatures = _enabledFeatures.asStateFlow()

    private val _appMode = MutableStateFlow(AppMode.SINGLE)
    val appMode = _appMode.asStateFlow()

    fun init() {
        _enabledFeatures.value = contextMenuOptionsRepository.getCurrentlyEnabledFeatures()
        _appMode.value = contextMenuOptionsRepository.getCurrentAppMode()
    }

    fun enableOrDisableFeature(feature: FeatureIds, enabled: Boolean) {
        viewModelScope.launch {
            contextMenuOptionsRepository.enableOrDisableFeature(feature, enabled)
            _enabledFeatures.value = contextMenuOptionsRepository.getCurrentlyEnabledFeatures()
        }
    }
}