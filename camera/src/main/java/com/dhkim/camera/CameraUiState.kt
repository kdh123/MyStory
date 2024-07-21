package com.dhkim.camera

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap

@Stable
data class CameraUiState(
    val isLoading: Boolean = false,
    val bitmap: Bitmap? = null,
    val backgroundBitmap: ImageBitmap? = null,
    val savedUrl: String = "",
    val isTimeStampMode: Boolean = false,
    val timeStamp: TimeStamp = TimeStamp()
)

data class TimeStamp(
    val date: String = "",
    val address: String = ""
)