package com.dhkim.domain.usecase

import com.dhkim.domain.repository.SettingRepository
import javax.inject.Inject

class UpdateNotificationSettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {

    suspend operator fun invoke(isChecked: Boolean) {
        settingRepository.updateNotificationSetting(isChecked = isChecked)
    }
}