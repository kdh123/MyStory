package com.dhkim.timecapsule.setting.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.setting.domain.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingRepository.getNotificationSetting().collect {
                _uiState.value = _uiState.value.copy(isNotificationChecked = it)
            }
        }
    }

    fun changeNotificationSetting(isChecked: Boolean) {
        viewModelScope.launch {
            settingRepository.updateNotificationSetting(isChecked)
        }
    }
}