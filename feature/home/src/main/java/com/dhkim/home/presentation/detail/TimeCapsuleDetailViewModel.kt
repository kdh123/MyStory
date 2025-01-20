package com.dhkim.home.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.Dispatcher
import com.dhkim.common.RestartableStateFlow
import com.dhkim.common.TimeCapsuleDispatchers
import com.dhkim.common.onetimeRestartableStateIn
import com.dhkim.home.domain.model.TimeCapsule
import com.dhkim.home.domain.usecase.DeleteTimeCapsuleUseCase
import com.dhkim.home.domain.usecase.GetTimeCapsuleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleDetailViewModel @Inject constructor(
    private val getTimeCapsuleUseCase: GetTimeCapsuleUseCase,
    private val deleteTimeCapsuleUseCase: DeleteTimeCapsuleUseCase,
    private val savedStateHandle: SavedStateHandle,
    @Dispatcher(TimeCapsuleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
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
        viewModelScope.launch(ioDispatcher) {
            val isSuccessful = deleteTimeCapsuleUseCase(timeCapsuleId = timeCapsuleId, isReceived = uiState.value.isReceived)

            if (isSuccessful) {
                _sideEffect.send(TimeCapsuleDetailSideEffect.Completed(isCompleted = true))
            } else {
                _sideEffect.send(TimeCapsuleDetailSideEffect.Message(message = "삭제에 실패하였습니다"))
            }
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