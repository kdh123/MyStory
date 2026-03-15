package com.dhkim.main.work

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dhkim.domain.usecase.CanOpenTimeCapsuleUseCase
import com.dhkim.domain.usecase.GetNotificationSettingUseCase
import com.dhkim.main.MainActivity
import com.dhkim.ui.NotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@HiltWorker
class CheckOpenableTimeCapsuleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val canOpenTimeCapsuleUseCase: CanOpenTimeCapsuleUseCase,
    private val getNotificationSettingUseCase: GetNotificationSettingUseCase,
    private val notificationManager: NotificationManager,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val isNotificationSettingOn = getNotificationSettingUseCase().first()
            if (canOpenTimeCapsuleUseCase()) {
                notificationManager.showNotification(
                    title = "알림",
                    desc = "오늘 개봉할 수 있는 타임캡슐이 존재합니다.",
                    intent = intent,
                    isNotificationSettingOn = isNotificationSettingOn
                )
            }

            Result.success()
        }
    }
}
