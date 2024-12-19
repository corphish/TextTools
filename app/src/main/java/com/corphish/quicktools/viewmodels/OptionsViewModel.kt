package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import com.corphish.quicktools.repository.ContextMenuOptionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OptionsViewModel @Inject constructor(contextMenuOptionsRepository: ContextMenuOptionsRepository) :
    ViewModel() {
    private val _enabledFeatures = MutableStateFlow(contextMenuOptionsRepository.getCurrentlyEnabledFeatures())
    val enabledFeatures = _enabledFeatures.asStateFlow()
}