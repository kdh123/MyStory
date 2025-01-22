package com.dhkim.setting.domain.usecase

import com.dhkim.setting.domain.repository.SettingRepository
import javax.inject.Inject

class UpdateNotificationSettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {

    suspend operator fun invoke(isChecked: Boolean) {
        settingRepository.updateNotificationSetting(isChecked = isChecked)
    }
}