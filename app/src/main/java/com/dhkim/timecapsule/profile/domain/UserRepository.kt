package com.dhkim.timecapsule.profile.domain

import com.dhkim.timecapsule.profile.data.dataSource.isSuccessful
import kotlinx.coroutines.flow.Flow

typealias isExist = Boolean

interface UserRepository {

    suspend fun getMyInfo(): Flow<User>
    suspend fun getMyId(): String
    suspend fun signUp(userId: String, fcmToken: String): isSuccessful
    fun updateUser(user: User)
    suspend fun searchUser(userId: String): Flow<isExist?>

    suspend fun addFriends(userId: String): Flow<isSuccessful>
    suspend fun addRequests(userId: String): Flow<isSuccessful>

    suspend fun getFcmToken(): String
    suspend fun updateLocalFcmToken(fcmToken: String)
    suspend fun getMyUuid(): String
    suspend fun updateUuid(uuid: String)
    suspend fun updateRemoteFcmToken(fcmToken: String)
    suspend fun registerPush(uuid: String, fcmToken: String): isSuccessful
}