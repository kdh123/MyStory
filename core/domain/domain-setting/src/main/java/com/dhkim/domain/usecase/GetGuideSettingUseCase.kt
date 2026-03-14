package com.dhkim.domain.usecase

import com.dhkim.domain.repository.SettingRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetGuideSettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {

    suspend operator fun invoke(): Boolean {
        return settingRepository.getGuideSetting().first()
    }
}