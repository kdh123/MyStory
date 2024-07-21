package com.dhkim.camera

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.CommonResult
import com.dhkim.common.DateUtil
import com.dhkim.location.domain.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<CameraSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun initAddress(lat: String, lng: String) {
        viewModelScope.launch {
            val timeStamp = _uiState.value.timeStamp
            val result = locationRepository.getAddress(lat, lng)

            when (result) {
                is CommonResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        timeStamp = timeStamp.copy(
                            date = DateUtil.currentTime(),
                            address = result.data?.placeName ?: "알 수 없음"
                        )
                    )
                }

                is CommonResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        timeStamp = timeStamp.copy(
                            date = DateUtil.currentTime(),
                            address = "알 수 없음"
                        )
                    )
                }
            }
        }
    }

    fun setTimeStampMode() {
        val isTimeStampMode = _uiState.value.isTimeStampMode
        _uiState.value = _uiState.value.copy(isTimeStampMode = !isTimeStampMode)
    }

    fun onTakePhoto(bitmap: Bitmap, backgroundBitmap: ImageBitmap) {
        _uiState.value = _uiState.value.copy(bitmap = bitmap, backgroundBitmap = backgroundBitmap)
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