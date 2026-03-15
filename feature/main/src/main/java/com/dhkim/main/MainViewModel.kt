package com.dhkim.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.domain.usecase.GetGuideSettingUseCase
import com.dhkim.domain.usecase.UpdateGuideSettingUseCase
import com.dhkim.ui.Popup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val getGuideSettingUseCase: GetGuideSettingUseCase,
    private val updateGuideSettingUseCase: UpdateGuideSettingUseCase,
) : ViewModel() {

    private val _showGuide = MutableSharedFlow<Boolean>()
    val showGuide = _showGuide.asSharedFlow()

    private val _currentPopup = MutableStateFlow<Popup?>(null)
    val currentPopup = _currentPopup.asStateFlow()

    fun showPopup(popup: Popup?) {
        _currentPopup.value = popup
    }

    init {
        viewModelScope.launch {
            val showGuide = getGuideSettingUseCase()
            _showGuide.emit(showGuide)
        }
    }

    fun closeGuideDialog() {
        viewModelScope.launch {
            _showGuide.emit(false)
        }
    }

    fun neverShowGuideAgain() {
        viewModelScope.launch {
            updateGuideSettingUseCase(show = false)
            _showGuide.emit(false)
        }
    }
}