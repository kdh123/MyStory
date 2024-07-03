package com.dhkim.user.data.dataSource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dhkim.database.AppDatabase
import com.dhkim.database.entity.FriendEntity
import com.dhkim.user.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val db: AppDatabase
) {

    private val PREF_KEY_USER_ID = stringPreferencesKey("userId")
    private val PREF_KEY_USER_PROFILE_IMAGE = intPreferencesKey("userProfileImage")
    private val PREF_KEY_FCM_TOKEN = stringPreferencesKey("fcmToken")
    private val PREF_KEY_UUID = stringPreferencesKey("uuid")

    private val friendService = db.friendDao()

    fun getAllFriend(): Flow<List<FriendEntity>?> {
        return friendService.getAllFriend()
    }

    fun getFriend(id: String): FriendEntity? {
        return friendService.getFriend(id)
    }

    fun saveFriend(friendEntity: FriendEntity) {
        friendService.saveFriend(friendEntity)
    }

    fun updateFriend(friendEntity: FriendEntity) {
        friendService.updateFriend(friendEntity)
    }

    fun deleteFriend(id: String) {
        friendService.deleteFriend(id)
    }

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
        dataStore.edit { settings ->
            settings[PREF_KEY_USER_PROFILE_IMAGE] = profileImage.toInt()
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