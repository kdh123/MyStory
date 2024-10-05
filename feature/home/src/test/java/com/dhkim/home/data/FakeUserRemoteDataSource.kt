package com.dhkim.home.data

import com.dhkim.common.CommonResult
import com.dhkim.user.datasource.UserRemoteDataSource
import com.dhkim.user.datasource.isSuccessful
import com.dhkim.user.model.Friend
import com.dhkim.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FakeUserRemoteDataSource @Inject constructor() : UserRemoteDataSource {
    override fun getMyInfo(myId: String): Flow<User> {
        return flowOf(User())
    }

    override fun searchUser(userId: String): Flow<CommonResult<User?>> {
        return flowOf(CommonResult.Success(User()))
    }

    override fun updateUser(user: User): Flow<isSuccessful> {
        return flowOf(true)
    }

    override fun acceptFriend(
        myId: String,
        myProfileImage: String,
        myUuid: String,
        userId: String,
        userProfileImage: String,
        userUuid: String
    ): Flow<isSuccessful> {
        return flowOf(true)
    }

    override fun addFriend(myId: String, myProfileImage: String, myUuid: String, userId: String, userProfileImage: String): Flow<isSuccessful> {
        return flowOf(true)
    }

    override fun deleteFriend(myId: String, userId: String): Flow<isSuccessful> {
        return flowOf(true)
    }

    override fun updateFriend(myId: String, friend: Friend): Flow<isSuccessful> {
        return flowOf(true)
    }

    override fun addRequest(myId: String, myProfileImage: String, userId: String): Flow<isSuccessful> {
        return flowOf(true)
    }

    override fun updateFcmToken(userId: String, uuid: String): Flow<isSuccessful> {
        return flowOf(true)
    }

    override suspend fun registerPush(uuid: String, fcmToken: String): CommonResult<Int> {
        return CommonResult.Success(0)
    }
}