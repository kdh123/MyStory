package com.dhkim.home.presentation.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.RestartableStateFlow
import com.dhkim.common.onetimeRestartableStateIn
import com.dhkim.story.domain.model.TimeCapsule
import com.dhkim.story.domain.usecase.GetAllTimeCapsuleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MoreTimeCapsuleViewModel @Inject constructor(
    private val getAllTimeCapsuleUseCase: GetAllTimeCapsuleUseCase
) : ViewModel() {

    val uiState: RestartableStateFlow<MoreTimeCapsuleUiState> = getAllTimeCapsuleUseCase()
        .map { it.toUiState() }
        .onetimeRestartableStateIn(
            scope = viewModelScope,
            initialValue = MoreTimeCapsuleUiState(),
            isOnetime = false
        )
}

fun List<TimeCapsule>.toUiState(): MoreTimeCapsuleUiState {
    val openedTimeCapsules = filter { it.isOpened }
        .sortedByDescending {
            it.date
        }

    return MoreTimeCapsuleUiState(
        isLoading = false,
        timeCapsules = openedTimeCapsules
    )
}