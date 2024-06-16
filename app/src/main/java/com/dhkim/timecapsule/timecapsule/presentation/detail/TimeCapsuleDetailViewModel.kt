package com.dhkim.timecapsule.timecapsule.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleDetailViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimeCapsuleOpenUiState())
    val uiState = _uiState.asStateFlow()

    fun init(timeCapsuleId: String, isReceived: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isReceived) {
                timeCapsuleRepository.getReceivedTimeCapsule(id = timeCapsuleId)?.let {
                    timeCapsuleRepository.updateReceivedTimeCapsule(it.copy(isOpened = true))
                    _uiState.value = _uiState.value.copy(timeCapsule = it.toTimeCapsule())
                }
            } else {
                timeCapsuleRepository.getMyTimeCapsule(id = timeCapsuleId)?.let {
                    timeCapsuleRepository.editMyTimeCapsule(it.copy(isOpened = true))
                    _uiState.value = _uiState.value.copy(timeCapsule = it.toTimeCapsule())
                }
            }
        }
    }
}