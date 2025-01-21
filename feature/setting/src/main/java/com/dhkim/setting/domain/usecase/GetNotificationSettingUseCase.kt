package com.dhkim.setting.domain.usecase

import com.dhkim.setting.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationSettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return settingRepository.getNotificationSetting()
    }
}