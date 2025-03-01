package com.dhkim.setting.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.setting.domain.usecase.GetNotificationSettingUseCase
import com.dhkim.setting.domain.usecase.UpdateNotificationSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SettingViewModel @Inject constructor(
    private val getNotificationSettingUseCase: GetNotificationSettingUseCase,
    private val updateNotificationSettingUseCase: UpdateNotificationSettingUseCase,
) : ViewModel() {

    val uiState = getNotificationSettingUseCase()
        .map { SettingUiState(isNotificationChecked = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingUiState()
        )

    fun changeNotificationSetting(isChecked: Boolean) {
        viewModelScope.launch {
            updateNotificationSettingUseCase(isChecked = isChecked)
        }
    }
}