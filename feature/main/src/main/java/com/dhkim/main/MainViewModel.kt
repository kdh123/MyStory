package com.dhkim.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dhkim.main.work.CheckOpenableTimeCapsuleWorker
import com.dhkim.setting.domain.usecase.GetGuideSettingUseCase
import com.dhkim.setting.domain.usecase.UpdateGuideSettingUseCase
import com.dhkim.ui.Popup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val CHECK_OPENABLE_TIME_CAPSULE_WORK_NAME = "checkOpenableTimeCapsuleWork"

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val getGuideSettingUseCase: GetGuideSettingUseCase,
    private val updateGuideSettingUseCase: UpdateGuideSettingUseCase,
    private val workManager: WorkManager
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

        val checkOpenableTimeCapsuleWorker = PeriodicWorkRequestBuilder<CheckOpenableTimeCapsuleWorker>(
            24, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            CHECK_OPENABLE_TIME_CAPSULE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            checkOpenableTimeCapsuleWorker
        )
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