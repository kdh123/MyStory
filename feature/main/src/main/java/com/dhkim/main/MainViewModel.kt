package com.dhkim.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dhkim.setting.domain.SettingRepository
import com.dhkim.main.work.CheckOpenableTimeCapsuleWorker
import com.dhkim.ui.Popup
import com.dhkim.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val CHECK_OPENABLE_TIME_CAPSULE_WORK_NAME = "checkOpenableTimeCapsuleWork"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingRepository: SettingRepository,
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
            val showGuide = settingRepository.getGuideSetting().first()
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
            settingRepository.updateGuideSetting(show = false)
            _showGuide.emit(false)
        }
    }

    fun updateFcmToken(fcmToken: String) {
        viewModelScope.launch {
            userRepository.run {
                /*if (getFcmToken().isEmpty()) {
                    registerPush(getMyUuid(), fcmToken)
                }*/
            }
        }
    }
}