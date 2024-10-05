package com.dhkim.user.datasource

import com.dhkim.database.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {

    fun getAllFriend(): Flow<List<FriendEntity>?>
    fun getFriend(id: String): FriendEntity?
    fun saveFriend(friendEntity: FriendEntity)
    fun updateFriend(friendEntity: FriendEntity)
    fun deleteFriend(id: String)
    suspend fun getUserId(): String
    suspend fun updateUserId(userId: String)
    suspend fun getProfileImage(): Int
    suspend fun updateProfileImage(profileImage: String)
    suspend fun getUuid(): String
    suspend fun updateUuid(uuid: String)
    suspend fun getFcmToken(): String
    suspend fun updateFcmToken(fcmToken: String)
}