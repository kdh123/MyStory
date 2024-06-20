package com.dhkim.timecapsule.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.dhkim.timecapsule.timecapsule.data.work.CheckOpenableTimeCapsuleWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val CHECK_OPENABLE_TIME_CAPSULE_WORK_NAME = "checkOpenableTimeCapsuleWork"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val workManager: WorkManager
) : ViewModel() {
    init {
        val checkOpenableTimeCapsuleWorker = PeriodicWorkRequestBuilder<CheckOpenableTimeCapsuleWorker>(
            6, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            CHECK_OPENABLE_TIME_CAPSULE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            checkOpenableTimeCapsuleWorker
        )
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