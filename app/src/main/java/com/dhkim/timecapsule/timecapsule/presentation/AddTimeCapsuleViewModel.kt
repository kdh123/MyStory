package com.dhkim.timecapsule.timecapsule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.timecapsule.domain.MyTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.SendTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTimeCapsuleViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTimeCapsuleUiState())
    val uiState = _uiState.asStateFlow()

    fun addTimeCapsule(type: TimeCapsuleType) {
        viewModelScope.launch(Dispatchers.IO) {
            when (type) {
                is TimeCapsuleType.My -> {
                    timeCapsuleRepository.saveMyTimeCapsule((type.timeCapsule as MyTimeCapsule))
                }

                is TimeCapsuleType.Send -> {
                    timeCapsuleRepository.saveSendTimeCapsule((type.timeCapsule as SendTimeCapsule))
                }
            }
        }
    }

    fun typing(str: String) {
        _uiState.value = _uiState.value.copy(content = str)
    }

    fun addImage(imageUrl: String) {
        val currentImageUrls = _uiState.value.imageUrls.toMutableList().apply {
            add(imageUrl)
        }
        _uiState.value = _uiState.value.copy(imageUrls = currentImageUrls)
    }

    fun setCheckLocation(isChecked: Boolean) {
        _uiState.value = _uiState.value.copy(checkLocation = isChecked)
    }

    fun setOpenDate(date: String) {
        _uiState.value = _uiState.value.copy(openDate = date)
    }
}