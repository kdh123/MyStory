package com.dhkim.setting.data.dataSource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val PREF_KEY_SETTING = booleanPreferencesKey("setting")
    private val PREF_KEY_SETTING_GUIDE = booleanPreferencesKey("settingGuide")

    fun getGuideSetting(): Flow<Boolean> {
        return dataStore.data
            .map { preferences ->
                preferences[PREF_KEY_SETTING_GUIDE] ?: true
            }
    }

    suspend fun updateGuideSetting(show: Boolean) {
        dataStore.edit { settings ->
            settings[PREF_KEY_SETTING_GUIDE] = show
        }
    }

    fun getNotificationSetting(): Flow<Boolean> {
        return dataStore.data
            .map { preferences ->
                preferences[PREF_KEY_SETTING] ?: true
            }
    }

    suspend fun updateNotificationSetting(isChecked: Boolean) {
        dataStore.edit { settings ->
            settings[PREF_KEY_SETTING] = isChecked
        }
    }
}