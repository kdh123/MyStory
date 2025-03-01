package com.dhkim.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.story.domain.usecase.GetReceivedAllTimeCapsuleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class NotificationViewModel @Inject constructor(
    private val getReceivedAllTimeCapsuleUseCase: GetReceivedAllTimeCapsuleUseCase
) : ViewModel() {

    val uiState: StateFlow<NotificationUiState> = getReceivedAllTimeCapsuleUseCase()
        .map { NotificationUiState(timeCapsules = it) }
        .catch { }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotificationUiState()
        )
}