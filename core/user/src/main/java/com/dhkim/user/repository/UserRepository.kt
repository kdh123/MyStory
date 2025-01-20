package com.dhkim.user.repository

import com.dhkim.common.CommonResult
import com.dhkim.user.datasource.isSuccessful
import com.dhkim.user.model.Friend
import com.dhkim.user.model.LocalFriend
import com.dhkim.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getFriend(id: String): LocalFriend?
    fun updateFriend(friend: Friend): Flow<isSuccessful>
    fun deleteLocalFriend(id: String)

    fun getMyInfo(myId: String): Flow<User>
    suspend fun getMyId(): String
    fun updateUser(user: User): Flow<isSuccessful>
    fun searchUser(userId: String): Flow<CommonResult<User?>>

    suspend fun addFriend(userId: String, userProfileImage: String): Flow<isSuccessful>
    fun deleteFriend(userId: String): Flow<isSuccessful>
    suspend fun addRequests(userId: String): Flow<isSuccessful>
    fun acceptFriend(userId: String, userProfileImage: String, userUuid: String): Flow<isSuccessful>

    suspend fun updateUserId(userId: String)
    suspend fun getFcmToken(): String
    suspend fun updateFcmToken(fcmToken: String)
    suspend fun updateLocalFcmToken(fcmToken: String)
    suspend fun getProfileImage(): Int
    suspend fun updateProfileImage(profileImage: String)
    suspend fun getMyUuid(): String
    suspend fun updateUuid(uuid: String)
    suspend fun updateRemoteFcmToken(fcmToken: String)
    suspend fun registerPush(uuid: String, fcmToken: String): isSuccessful
}