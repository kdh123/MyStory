package com.dhkim.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<CameraSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun onTakePhoto(bitmap: Bitmap) {
        _uiState.value = _uiState.value.copy(bitmap = bitmap)
    }

    fun onSavingPhoto() {
        _uiState.value = _uiState.value.copy(isLoading = true)
    }

    fun onSavedPhoto(savedUrl: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = false, savedUrl = savedUrl)
            _sideEffect.emit(CameraSideEffect.Completed(isCompleted = true))
        }
    }
}