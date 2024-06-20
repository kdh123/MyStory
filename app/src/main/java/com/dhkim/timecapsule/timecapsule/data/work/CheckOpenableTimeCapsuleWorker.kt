package com.dhkim.timecapsule.timecapsule.data.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dhkim.timecapsule.common.DateUtil
import com.dhkim.timecapsule.common.presentation.NotificationManager
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsuleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

@HiltWorker
class CheckOpenableTimeCapsuleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val timeCapsuleRepository: TimeCapsuleRepository
) : CoroutineWorker(context, params) {

    private val notificationManager = NotificationManager(context)
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
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
                notificationManager.showNotification("알림", "오늘 오픈할 수 있는 타임캡슐이 존재합니다.")
            }

            Result.success()
        }
    }
}