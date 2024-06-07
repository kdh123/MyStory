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

    private val PREF_KEY_USER = stringPreferencesKey("prefKeyUser")
    private val PREF_KEY_FCM_TOKEN = stringPreferencesKey("fcmToken")

    suspend fun getUserId(): String {
        return dataStore.data
            .map { preferences ->
                preferences[PREF_KEY_USER] ?: ""
            }.first()
    }

    suspend fun updateUserId(userId: String) {
        dataStore.edit { settings ->
            settings[PREF_KEY_USER] = userId
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