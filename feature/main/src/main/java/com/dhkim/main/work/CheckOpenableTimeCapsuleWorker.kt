package com.dhkim.main.work

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dhkim.common.NotificationManager
import com.dhkim.main.MainActivity
import com.dhkim.setting.domain.usecase.GetNotificationSettingUseCase
import com.dhkim.story.domain.usecase.CanOpenTimeCapsuleUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltWorker
class CheckOpenableTimeCapsuleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val canOpenTimeCapsuleUseCase: CanOpenTimeCapsuleUseCase,
    private val getNotificationSettingUseCase: GetNotificationSettingUseCase,
) : CoroutineWorker(context, params) {

    private val workerContext = context

    @Inject
    lateinit var notificationManager: NotificationManager

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val intent = Intent(workerContext, MainActivity::class.java).apply {
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