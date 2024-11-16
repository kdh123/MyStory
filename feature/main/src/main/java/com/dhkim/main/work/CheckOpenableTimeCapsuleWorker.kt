package com.dhkim.main.work

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dhkim.common.DateUtil
import com.dhkim.common.NotificationManager
import com.dhkim.main.MainActivity
import com.dhkim.home.domain.repository.TimeCapsuleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltWorker
class CheckOpenableTimeCapsuleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val settingRepository: com.dhkim.setting.domain.SettingRepository,
) : CoroutineWorker(context, params) {

    private val workerContext = context

    @Inject
    lateinit var notificationManager: NotificationManager

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val intent = Intent(workerContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val isNotificationSettingOn = settingRepository.getNotificationSetting().first()
            val isOpenableTimeCapsuleExist = combine(
                timeCapsuleRepository.getMyAllTimeCapsule(),
                timeCapsuleRepository.getReceivedAllTimeCapsule()
            ) { myTimeCapsules, receivedTimeCapsules ->
                val openableMyTimeCapsules = myTimeCapsules
                    .filter { (!it.isOpened && DateUtil.isAfter(strDate = it.openDate)) }
                val openableReceivedTimeCapsules = receivedTimeCapsules
                    .filter { (!it.isOpened && DateUtil.isAfter(strDate = it.openDate)) }

                openableMyTimeCapsules.isNotEmpty() || openableReceivedTimeCapsules.isNotEmpty()
            }.catch { }.firstOrNull() ?: false

            if (isOpenableTimeCapsuleExist) {
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