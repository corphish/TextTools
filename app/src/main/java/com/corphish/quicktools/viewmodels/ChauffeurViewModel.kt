package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.common.FeatureStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChauffeurViewModel @Inject constructor() : ViewModel() {
    private val _modelDownloadFlow = MutableStateFlow<GenerativeModelStatus>(GenerativeModelStatus.Initial)
    val modelDownloadFlow: StateFlow<GenerativeModelStatus> = _modelDownloadFlow.asStateFlow()
    val _generationClient = Generation.getClient()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            checkModelStatus()
        }
    }

    private suspend fun checkModelStatus() {
        val status = _generationClient.checkStatus()
        when (status) {
            FeatureStatus.AVAILABLE -> _modelDownloadFlow.emit(GenerativeModelStatus.Available)
            FeatureStatus.UNAVAILABLE -> _modelDownloadFlow.emit(GenerativeModelStatus.NotAvailable)
            FeatureStatus.DOWNLOADABLE -> _modelDownloadFlow.emit(GenerativeModelStatus.Downloadable)
        }
    }

    fun downloadModel() {
        viewModelScope.launch(Dispatchers.Default) {
            _generationClient.download().collect {
                when (it) {
                    is DownloadStatus.DownloadStarted -> _modelDownloadFlow.emit(GenerativeModelStatus.DownloadStarting)
                    DownloadStatus.DownloadCompleted -> _modelDownloadFlow.emit(GenerativeModelStatus.DownloadCompleted)
                    is DownloadStatus.DownloadFailed -> _modelDownloadFlow.emit(GenerativeModelStatus.DownloadFailed(it.e.toString()))
                    is DownloadStatus.DownloadProgress -> _modelDownloadFlow.emit(
                        GenerativeModelStatus.Downloading(it.totalBytesDownloaded.toString()))
                }
            }
        }
    }
}

sealed class GenerativeModelStatus {
    object Initial: GenerativeModelStatus()
    object Available: GenerativeModelStatus()
    object NotAvailable: GenerativeModelStatus()
    object Downloadable: GenerativeModelStatus()

    object DownloadStarting: GenerativeModelStatus()
    data class Downloading(val bytesDownloaded: String): GenerativeModelStatus()
    object DownloadCompleted: GenerativeModelStatus()
    data class DownloadFailed(val error: String): GenerativeModelStatus()
}