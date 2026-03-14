package com.dhkim.domain.usecase

import com.dhkim.domain.model.ReceivedTimeCapsule
import com.dhkim.domain.repository.TimeCapsuleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReceivedAllTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {

    operator fun invoke(): Flow<List<ReceivedTimeCapsule>> {
        return timeCapsuleRepository.getReceivedAllTimeCapsule()
    }
}