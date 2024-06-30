package com.dhkim.user.domain

import com.dhkim.common.CommonResult
import com.dhkim.user.data.dataSource.isSuccessful
import kotlinx.coroutines.flow.Flow

interface UserRepository {

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