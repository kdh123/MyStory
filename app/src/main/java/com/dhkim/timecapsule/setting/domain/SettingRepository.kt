package com.dhkim.timecapsule.setting.domain

import kotlinx.coroutines.flow.Flow

interface SettingRepository {

    suspend fun getGuideSetting(): Flow<Boolean>
    suspend fun updateGuideSetting(show: Boolean)
    suspend fun getNotificationSetting(): Flow<Boolean>
    suspend fun updateNotificationSetting(isChecked: Boolean)
}