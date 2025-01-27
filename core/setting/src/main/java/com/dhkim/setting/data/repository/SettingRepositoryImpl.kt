package com.dhkim.setting.data.repository

import com.dhkim.setting.data.dataSource.SettingLocalDataSource
import com.dhkim.setting.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SettingRepositoryImpl @Inject constructor(
    private val localDataSource: SettingLocalDataSource
) : SettingRepository {

    override fun getGuideSetting(): Flow<Boolean> {
        return localDataSource.getGuideSetting()
    }

    override suspend fun updateGuideSetting(show: Boolean) {
        localDataSource.updateGuideSetting(show)
    }

    override fun getNotificationSetting(): Flow<Boolean> {
        return localDataSource.getNotificationSetting()
    }

    override suspend fun updateNotificationSetting(isChecked: Boolean) {
        localDataSource.updateNotificationSetting(isChecked)
    }
}