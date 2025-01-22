package com.dhkim.setting.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingRepository {

    fun getGuideSetting(): Flow<Boolean>
    suspend fun updateGuideSetting(show: Boolean)
    fun getNotificationSetting(): Flow<Boolean>
    suspend fun updateNotificationSetting(isChecked: Boolean)
}