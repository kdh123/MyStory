package com.dhkim.timecapsule.setting.domain

import kotlinx.coroutines.flow.Flow

interface SettingRepository {

    suspend fun getNotificationSetting(): Flow<Boolean>
    suspend fun updateNotificationSetting(isChecked: Boolean)
}