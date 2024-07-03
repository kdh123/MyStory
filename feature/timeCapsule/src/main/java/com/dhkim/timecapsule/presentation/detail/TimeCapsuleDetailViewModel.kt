package com.dhkim.timecapsule.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.domain.TimeCapsuleRepository
import com.dhkim.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleDetailViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimeCapsuleDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<TimeCapsuleDetailSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun init(timeCapsuleId: String, isReceived: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val myId = userRepository.getMyId()
            val myProfileImage = "${userRepository.getProfileImage()}"
            if (isReceived) {
                timeCapsuleRepository.getReceivedTimeCapsule(id = timeCapsuleId)?.let {
                    timeCapsuleRepository.updateReceivedTimeCapsule(it.copy(isOpened = true))
                    _uiState.value = _uiState.value.copy(isReceived = true, timeCapsule = it.toTimeCapsule())
                }
            } else {
                timeCapsuleRepository.getMyTimeCapsule(id = timeCapsuleId)?.let {
                    timeCapsuleRepository.editMyTimeCapsule(it.copy(isOpened = true))
                    _uiState.value = _uiState.value.copy(isReceived = false, timeCapsule = it.toTimeCapsule(myId, myProfileImage))
                }
            }
        }
    }

    fun deleteTImeCapsule(timeCapsuleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_uiState.value.isReceived) {
                timeCapsuleRepository.deleteReceivedTimeCapsule(timeCapsuleId)
            } else {
                timeCapsuleRepository.deleteMyTimeCapsule(timeCapsuleId)
            }
            _sideEffect.emit(TimeCapsuleDetailSideEffect.Completed(isCompleted = true))
        }
    }
}