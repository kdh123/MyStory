package com.dhkim.camera

import android.graphics.Bitmap

data class CameraUiState(
    val isLoading: Boolean = false,
    val bitmap: Bitmap? = null,
    val backgroundBitmap: Bitmap? = null,
    val savedUrl: String = ""
)