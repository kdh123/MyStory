package com.dhkim.setting.domain.usecase

import com.dhkim.setting.domain.repository.SettingRepository
import javax.inject.Inject

class UpdateGuideSettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {

    suspend operator fun invoke(show: Boolean) {
        settingRepository.updateGuideSetting(show = show)
    }
}