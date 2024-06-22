package com.dhkim.timecapsule.setting.data.repository

import com.dhkim.timecapsule.setting.data.dataSource.SettingLocalDataSource
import com.dhkim.timecapsule.setting.domain.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val localDataSource: SettingLocalDataSource
) : SettingRepository {

    override suspend fun getNotificationSetting(): Flow<Boolean> {
        return localDataSource.getNotificationSetting()
    }

    override suspend fun updateNotificationSetting(isChecked: Boolean) {
        localDataSource.updateNotificationSetting(isChecked)
    }
}