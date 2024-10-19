package com.dhkim.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.RestartableStateFlow
import com.dhkim.common.onetimeRestartableStateIn
import com.dhkim.home.domain.DeleteTimeCapsuleUseCase
import com.dhkim.home.domain.GetAllTimeCapsuleUseCase
import com.dhkim.home.domain.TimeCapsuleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleViewModel @Inject constructor(
    private val getAllTimeCapsuleUseCase: GetAllTimeCapsuleUseCase,
    private val deleteTimeCapsuleUseCase: DeleteTimeCapsuleUseCase
) : ViewModel() {

    val uiState: RestartableStateFlow<TimeCapsuleUiState> = getAllTimeCapsuleUseCase()
        .map { it.toUiState() }
        .onetimeRestartableStateIn(
            scope = viewModelScope,
            initialValue = TimeCapsuleUiState(),
            isOnetime = false
        )

    private val _sideEffect = Channel<TimeCapsuleSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun deleteTimeCapsule(timeCapsuleId: String, isReceived: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSuccessful = deleteTimeCapsuleUseCase(timeCapsuleId, isReceived)
            if (!isSuccessful) {
                _sideEffect.send(TimeCapsuleSideEffect.Message("삭제에 실패하였습니다."))
            }
        }
    }
}

fun List<TimeCapsuleItem>.toUiState(): TimeCapsuleUiState {
    return TimeCapsuleUiState(
        isLoading = false,
        timeCapsules = this.toImmutableList()
    )
}