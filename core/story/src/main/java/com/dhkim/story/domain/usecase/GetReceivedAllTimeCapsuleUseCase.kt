package com.dhkim.story.domain.usecase

import com.dhkim.story.domain.model.ReceivedTimeCapsule
import com.dhkim.story.domain.repository.TimeCapsuleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReceivedAllTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {

    operator fun invoke(): Flow<List<ReceivedTimeCapsule>> {
        return timeCapsuleRepository.getReceivedAllTimeCapsule()
    }
}