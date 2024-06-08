package com.dhkim.timecapsule.profile.data.dataSource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val PREF_KEY_USER_ID = stringPreferencesKey("userId")
    private val PREF_KEY_FCM_TOKEN = stringPreferencesKey("fcmToken")
    private val PREF_KEY_UUID = stringPreferencesKey("uuid")

    suspend fun getUserId(): String {
        return dataStore.data
            .map { preferences ->
                preferences[PREF_KEY_USER_ID] ?: ""
            }.first()
    }

    suspend fun updateUserId(userId: String) {
        dataStore.edit { settings ->
            settings[PREF_KEY_USER_ID] = userId
        }
    }

    suspend fun getUuid(): String {
        return dataStore.data
            .map { preferences ->
                preferences[PREF_KEY_UUID] ?: ""
            }.first()
    }

    suspend fun updateUuid(uuid: String) {
        dataStore.edit { settings ->
            settings[PREF_KEY_UUID] = uuid
        }
    }

    suspend fun getFcmToken(): String {
        return dataStore.data
            .map { preferences ->
                preferences[PREF_KEY_FCM_TOKEN] ?: ""
            }.first()
    }

    suspend fun updateFcmToken(fcmToken: String) {
        dataStore.edit { settings ->
            settings[PREF_KEY_FCM_TOKEN] = fcmToken
        }
    }
}