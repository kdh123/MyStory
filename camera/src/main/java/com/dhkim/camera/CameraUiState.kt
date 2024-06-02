package com.dhkim.camera

import android.graphics.Bitmap

data class CameraUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val bitmap: Bitmap? = null,
    val savedUrl: String = ""
)