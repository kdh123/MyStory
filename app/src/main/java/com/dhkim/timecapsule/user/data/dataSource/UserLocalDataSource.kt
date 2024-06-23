package com.dhkim.timecapsule.user.data.dataSource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dhkim.timecapsule.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val PREF_KEY_USER_ID = stringPreferencesKey("userId")
    private val PREF_KEY_USER_PROFILE_IMAGE = intPreferencesKey("userProfileImage")
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

    suspend fun getProfileImage(): Int {
        return dataStore.data
            .map { preferences ->
                preferences[PREF_KEY_USER_PROFILE_IMAGE] ?: R.drawable.ic_smile_blue
            }.first()
    }

    suspend fun updateProfileImage(profileImage: String) {
        val image = when (profileImage) {
            "0" -> R.drawable.ic_smile_blue
            "1" -> R.drawable.ic_smile_violet
            "2" -> R.drawable.ic_smile_green
            "3" -> R.drawable.ic_smile_orange
            else -> R.drawable.ic_smile_red
        }

        dataStore.edit { settings ->
            settings[PREF_KEY_USER_PROFILE_IMAGE] = image
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