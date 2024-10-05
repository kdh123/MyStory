package com.dhkim.user.repository

import com.dhkim.common.CommonResult
import com.dhkim.user.datasource.isSuccessful
import com.dhkim.user.model.Friend
import com.dhkim.user.model.LocalFriend
import com.dhkim.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getAllFriend(): Flow<List<LocalFriend>>
    fun getFriend(id: String): LocalFriend?
    fun saveFriend(localFriend: LocalFriend)
    suspend fun updateFriend(friend: Friend)
    fun deleteLocalFriend(id: String)

    suspend fun getMyInfo(): Flow<User>
    suspend fun getMyId(): String
    suspend fun signUp(userId: String, profileImage: String, fcmToken: String): isSuccessful
    fun updateUser(user: User)
    suspend fun searchUser(userId: String): Flow<CommonResult<User?>>

    suspend fun addFriend(userId: String, userProfileImage: String): Flow<isSuccessful>
    suspend fun deleteFriend(userId: String): Flow<isSuccessful>
    suspend fun addRequests(userId: String): Flow<isSuccessful>
    suspend fun acceptFriend(userId: String, userProfileImage: String, userUuid: String): Flow<isSuccessful>

    suspend fun getFcmToken(): String
    suspend fun updateLocalFcmToken(fcmToken: String)
    suspend fun getProfileImage(): Int
    suspend fun updateProfileImage(profileImage: String)
    suspend fun getMyUuid(): String
    suspend fun updateUuid(uuid: String)
    suspend fun updateRemoteFcmToken(fcmToken: String)
    suspend fun registerPush(uuid: String, fcmToken: String): isSuccessful
}