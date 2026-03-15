package com.dhkim.domain.usecase

import com.dhkim.domain.repository.SettingRepository
import javax.inject.Inject

class UpdateGuideSettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {

    suspend operator fun invoke(show: Boolean) {
        settingRepository.updateGuideSetting(show = show)
    }
}