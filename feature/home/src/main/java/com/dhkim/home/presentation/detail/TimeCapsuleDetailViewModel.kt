package com.dhkim.home.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.home.domain.TimeCapsuleRepository
import com.dhkim.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleDetailViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var timeCapsuleIdParam = ""
    private var isReceivedParam = false

    private val _uiState = MutableStateFlow(TimeCapsuleDetailUiState())
    val uiState = _uiState
        .onStart {
            init(timeCapsuleId = timeCapsuleIdParam, isReceived = isReceivedParam)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimeCapsuleDetailUiState())

    private val _sideEffect = Channel<TimeCapsuleDetailSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onAction(action: TimeCapsuleDetailAction) {
        when (action) {
            is TimeCapsuleDetailAction.InitParams -> {
                timeCapsuleIdParam = action.timeCapsuleId
                isReceivedParam = action.isReceived
            }

            is TimeCapsuleDetailAction.Init -> {
                init(timeCapsuleId = action.timeCapsuleId, isReceived = action.isReceived)
            }

            is TimeCapsuleDetailAction.DeleteTimeCapsule -> {
                deleteTImeCapsule(action.timeCapsuleId)
            }
        }
    }

    private fun init(timeCapsuleId: String, isReceived: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val myId = userRepository.getMyId()
            val myProfileImage = "${userRepository.getProfileImage()}"
            if (isReceived) {
                timeCapsuleRepository.getReceivedTimeCapsule(id = timeCapsuleId)?.let {
                    timeCapsuleRepository.updateReceivedTimeCapsule(it.copy(isOpened = true))
                    val nickname = userRepository.getFriend(it.sender)?.nickname ?: it.sender
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isReceived = true,
                        timeCapsule = it.toTimeCapsule(nickname)
                    )
                }
            } else {
                timeCapsuleRepository.getMyTimeCapsule(id = timeCapsuleId)?.let {
                    timeCapsuleRepository.editMyTimeCapsule(it.copy(isOpened = true))
                    val sharedFriends = it.sharedFriends.map { userId ->
                        userRepository.getFriend(userId)?.nickname ?: userId
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isReceived = false,
                        timeCapsule = it.toTimeCapsule(myId, myProfileImage, sharedFriends)
                    )
                }
            }
        }
    }

    private fun deleteTImeCapsule(timeCapsuleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_uiState.value.isReceived) {
                timeCapsuleRepository.deleteReceivedTimeCapsule(timeCapsuleId)
            } else {
                timeCapsuleRepository.deleteMyTimeCapsule(timeCapsuleId)
            }
            _sideEffect.send(TimeCapsuleDetailSideEffect.Completed(isCompleted = true))
        }
    }
}