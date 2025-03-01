package com.dhkim.user.data.datasource

import com.dhkim.common.CommonResult
import com.dhkim.user.domain.model.Friend
import com.dhkim.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {

    fun getMyInfo(myId: String): Flow<User>

    fun searchUser(userId: String): Flow<CommonResult<User?>>

    fun updateUser(user: User): Flow<isSuccessful>

    fun acceptFriend(
        myId: String,
        myProfileImage: String,
        myUuid: String,
        userId: String,
        userProfileImage: String,
        userUuid: String
    ): Flow<isSuccessful>

    fun addFriend(myId: String, myProfileImage: String, myUuid: String, userId: String, userProfileImage: String): Flow<isSuccessful>

    fun deleteFriend(myId: String, userId: String): Flow<isSuccessful>

    fun updateFriend(myId: String, friend: Friend): Flow<isSuccessful>

    fun addRequest(myId: String, myProfileImage: String, userId: String): Flow<isSuccessful>

    fun updateFcmToken(userId: String, uuid: String): Flow<isSuccessful>

    suspend fun registerPush(uuid: String, fcmToken: String): CommonResult<Int>
}