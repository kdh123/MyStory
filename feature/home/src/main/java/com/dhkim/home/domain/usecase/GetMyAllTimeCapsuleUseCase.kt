package com.dhkim.home.domain.usecase

import com.dhkim.home.domain.model.MyTimeCapsule
import com.dhkim.home.domain.repository.TimeCapsuleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyAllTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {

    operator fun invoke(): Flow<List<MyTimeCapsule>> {
        return timeCapsuleRepository.getMyAllTimeCapsule()
    }
}