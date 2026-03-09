package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextCountViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val _characterCount = MutableStateFlow(0)
    val characterCount = _characterCount.asStateFlow()

    private val _letterCount = MutableStateFlow(0)
    val letterCount = _letterCount.asStateFlow()

    private val _digitCount = MutableStateFlow(0)
    val digitCount = _digitCount.asStateFlow()

    private val _wordCount = MutableStateFlow(0)
    val wordCount = _wordCount.asStateFlow()

    private val _spaceCount = MutableStateFlow(0)
    val spaceCount = _spaceCount.asStateFlow()

    private val _symbolCount = MutableStateFlow(0)
    val symbolCount = _symbolCount.asStateFlow()

    private val _wordFrequency = MutableStateFlow(listOf<Pair<String, Int>>())
    val wordFrequency = _wordFrequency.asStateFlow()

    // PIP state is one time query only on launch.
    // But we would like to query it from background.
    fun shouldLaunchInPIP(onDetermined: (Boolean) -> Unit) {
        viewModelScope.launch {
            onDetermined(settingsRepository.getLaunchInPIP())
        }
    }

    fun setTextAndProcess(text: String) {
        viewModelScope.launch {
            _text.value = text

            val freq = mutableMapOf<String, Int>()
            val list = mutableListOf<Pair<String, Int>>()

            _characterCount.value = 0
            _letterCount.value = 0
            _digitCount.value = 0
            _wordCount.value = 0
            _spaceCount.value = 0
            _symbolCount.value = 0
            _wordFrequency.value = mutableListOf()

            _characterCount.value = text.length
            for (c in text.toCharArray()) {
                if (c == ' ') {
                    _spaceCount.value += 1
                } else if (Character.isLetter(c)) {
                    _letterCount.value += 1
                } else if (Character.isDigit(c)) {
                    _digitCount.value += 1
                } else {
                    _symbolCount.value += 1
                }
            }

            for (w in text.split(" ")) {
                if (w.isEmpty()) {
                    continue
                }

                _wordCount.value += 1
                freq[w] = (freq[w] ?: 0) + 1
            }

            for (e in freq.entries) {
                list += e.key to e.value
            }

            list.sortBy { -it.second }
            _wordFrequency.value = list.toList()
        }
    }
}