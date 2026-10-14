package com.instagrab.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.instagrab.app.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InstagramRepository(application)

    private val _postState = MutableStateFlow<PostState>(PostState.Idle)
    val postState: StateFlow<PostState> = _postState.asStateFlow()

    private val _downloadStates = MutableStateFlow<Map<Int, DownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<Int, DownloadState>> = _downloadStates.asStateFlow()

    private val _serverConnected = MutableStateFlow<Boolean?>(null)
    val serverConnected: StateFlow<Boolean?> = _serverConnected.asStateFlow()

    private val _urlInput = MutableStateFlow("")
    val urlInput: StateFlow<String> = _urlInput.asStateFlow()

    private val _serverUrl = MutableStateFlow("")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    init {
        checkServerConnection()
    }

    fun onUrlChanged(value: String) { _urlInput.value = value }
    fun onServerUrlChanged(value: String) { _serverUrl.value = value }
    fun clearSnack() { _snackMessage.value = null }

    fun saveServerUrl() {
        repository.updateServerUrl(_serverUrl.value)
        checkServerConnection()
    }

    fun checkServerConnection() {
        viewModelScope.launch {
            _serverConnected.value = null
            _serverConnected.value = repository.checkHealth()
        }
    }

    fun fetchPost(url: String = _urlInput.value) {
        if (url.isBlank()) { _postState.value = PostState.Error("Lütfen bir Instagram linki girin"); return }
        viewModelScope.launch {
            _postState.value = PostState.Loading
            _downloadStates.value = emptyMap()
            val result = repository.getPostInfo(url)
            _postState.value = result.fold(
                onSuccess = { PostState.Success(it) },
                onFailure = { PostState.Error(it.message ?: "Hata") }
            )
        }
    }

    fun downloadVideo(shortcode: String, videoIndex: Int) {
        viewModelScope.launch {
            repository.downloadVideo(shortcode, videoIndex).collect { progress ->
                when (progress) {
                    is DownloadProgress.Started  -> updateDl(videoIndex, DownloadState.Downloading(videoIndex, 0))
                    is DownloadProgress.Progress -> updateDl(videoIndex, DownloadState.Downloading(videoIndex, progress.percent))
                    is DownloadProgress.Done     -> {
                        updateDl(videoIndex, DownloadState.Success(videoIndex, progress.filePath))
                        _snackMessage.value = "✅ İndirildi: ${progress.filePath.substringAfterLast("/")}"
                    }
                    is DownloadProgress.Error    -> {
                        updateDl(videoIndex, DownloadState.Error(videoIndex, progress.message))
                        _snackMessage.value = "❌ ${progress.message}"
                    }
                }
            }
        }
    }

    fun clearPost() { _postState.value = PostState.Idle; _downloadStates.value = emptyMap(); _urlInput.value = "" }

    fun handleSharedUrl(url: String) { _urlInput.value = url; fetchPost(url) }

    private fun updateDl(index: Int, state: DownloadState) {
        _downloadStates.value = _downloadStates.value.toMutableMap().also { it[index] = state }
    }
}
