package com.dhkim.story.domain.usecase

import com.dhkim.common.DateUtil
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

class CanOpenTimeCapsuleUseCase @Inject constructor(
    private val getMyAllTimeCapsuleUseCase: GetMyAllTimeCapsuleUseCase,
    private val getReceivedAllTimeCapsuleUseCase: GetReceivedAllTimeCapsuleUseCase
) {

    suspend operator fun invoke(): Boolean {
        return getMyAllTimeCapsuleUseCase().zip(
            getReceivedAllTimeCapsuleUseCase()
        ) { myTimeCapsules, receivedTimeCapsules ->
            val openableMyTimeCapsules = myTimeCapsules
                .filter { (!it.isOpened && DateUtil.isAfter(strDate = it.openDate)) }
            val openableReceivedTimeCapsules = receivedTimeCapsules
                .filter { (!it.isOpened && DateUtil.isAfter(strDate = it.openDate)) }

            openableMyTimeCapsules.isNotEmpty() || openableReceivedTimeCapsules.isNotEmpty()
        }.catch { }.firstOrNull() ?: false
    }
}