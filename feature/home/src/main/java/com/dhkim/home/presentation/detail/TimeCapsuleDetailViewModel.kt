package com.dhkim.home.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.RestartableStateFlow
import com.dhkim.common.onetimeRestartableStateIn
import com.dhkim.home.domain.GetTimeCapsuleUseCase
import com.dhkim.home.domain.TimeCapsule
import com.dhkim.home.domain.TimeCapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleDetailViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val getTimeCapsuleUseCase: GetTimeCapsuleUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val timeCapsuleId = savedStateHandle.get<String>("id") ?: ""
    val isReceived = savedStateHandle.get<String>("isReceived") ?: "false"
    val uiState: RestartableStateFlow<TimeCapsuleDetailUiState> = getTimeCapsuleUseCase(
        timeCapsuleId = timeCapsuleId,
        isReceived = isReceived.toBoolean()
    ).map { it.toUiState() }
        .onetimeRestartableStateIn(
            scope = viewModelScope,
            initialValue = TimeCapsuleDetailUiState()
        )

    private val _sideEffect = Channel<TimeCapsuleDetailSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onAction(action: TimeCapsuleDetailAction) {
        when (action) {
            is TimeCapsuleDetailAction.DeleteTimeCapsule -> {
                deleteTImeCapsule()
            }
        }
    }

    private fun deleteTImeCapsule() {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value.isReceived) {
                timeCapsuleRepository.deleteReceivedTimeCapsule(timeCapsuleId)
            } else {
                timeCapsuleRepository.deleteMyTimeCapsule(timeCapsuleId)
            }
            _sideEffect.send(TimeCapsuleDetailSideEffect.Completed(isCompleted = true))
        }
    }
}

fun TimeCapsule.toUiState(): TimeCapsuleDetailUiState {
    return TimeCapsuleDetailUiState(
        isLoading = false,
        isReceived = isReceived,
        timeCapsule = this
    )
}