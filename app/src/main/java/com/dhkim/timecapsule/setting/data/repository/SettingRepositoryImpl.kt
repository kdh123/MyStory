package com.dhkim.timecapsule.setting.data.repository

import com.dhkim.timecapsule.setting.data.dataSource.SettingLocalDataSource
import com.dhkim.timecapsule.setting.domain.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val localDataSource: SettingLocalDataSource
) : SettingRepository {

    override suspend fun getGuideSetting(): Flow<Boolean> {
        return localDataSource.getGuideSetting()
    }

    override suspend fun updateGuideSetting(show: Boolean) {
        localDataSource.updateGuideSetting(show)
    }

    override suspend fun getNotificationSetting(): Flow<Boolean> {
        return localDataSource.getNotificationSetting()
    }

    override suspend fun updateNotificationSetting(isChecked: Boolean) {
        localDataSource.updateNotificationSetting(isChecked)
    }
}