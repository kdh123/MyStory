package com.dhkim.domain.usecase

import com.dhkim.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationSettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return settingRepository.getNotificationSetting()
    }
}