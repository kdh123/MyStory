package com.dhkim.setting.domain.usecase

import com.dhkim.setting.domain.repository.SettingRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetGuideSettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {

    suspend operator fun invoke(): Boolean {
        return settingRepository.getGuideSetting().first()
    }
}