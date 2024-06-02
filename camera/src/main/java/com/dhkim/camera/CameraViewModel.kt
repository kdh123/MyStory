package com.dhkim.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState = _uiState.asStateFlow()

    fun onTakePhoto(bitmap: Bitmap) {
        _uiState.value = _uiState.value.copy(isLoading = true, bitmap = bitmap)
    }

    fun onSavedPhoto(savedUrl: String) {
        _uiState.value = _uiState.value.copy(isLoading = false, isCompleted = true, savedUrl = savedUrl)
    }
}