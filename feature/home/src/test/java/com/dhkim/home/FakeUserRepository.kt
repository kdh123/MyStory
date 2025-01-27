package com.dhkim.home

import com.dhkim.common.CommonResult
import com.dhkim.user.data.datasource.isSuccessful
import com.dhkim.user.domain.model.Friend
import com.dhkim.user.domain.model.LocalFriend
import com.dhkim.user.domain.model.User
import com.dhkim.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class FakeUserRepository : UserRepository {

    override fun getFriend(id: String): LocalFriend? {
        TODO("Not yet implemented")
    }

    override fun updateFriend(friend: Friend): Flow<isSuccessful> {
        TODO("Not yet implemented")
    }

    override fun deleteLocalFriend(id: String) {
        TODO("Not yet implemented")
    }

    override fun getMyInfo(myId: String): Flow<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyId(): String {
        TODO("Not yet implemented")
    }

    override fun updateUser(user: User): Flow<isSuccessful> {
        TODO("Not yet implemented")
    }

    override fun searchUser(userId: String): Flow<CommonResult<User?>> {
        TODO("Not yet implemented")
    }

    override suspend fun addFriend(userId: String, userProfileImage: String): Flow<isSuccessful> {
        TODO("Not yet implemented")
    }

    override fun deleteFriend(userId: String): Flow<isSuccessful> {
        TODO("Not yet implemented")
    }

    override suspend fun addRequests(userId: String): Flow<isSuccessful> {
        TODO("Not yet implemented")
    }

    override fun acceptFriend(userId: String, userProfileImage: String, userUuid: String): Flow<isSuccessful> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserId(userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getFcmToken(): String {
        TODO("Not yet implemented")
    }

    override suspend fun updateFcmToken(fcmToken: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateLocalFcmToken(fcmToken: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getProfileImage(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateProfileImage(profileImage: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getMyUuid(): String {
        TODO("Not yet implemented")
    }

    override suspend fun updateUuid(uuid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateRemoteFcmToken(fcmToken: String) {
        TODO("Not yet implemented")
    }

    override suspend fun registerPush(uuid: String, fcmToken: String): isSuccessful {
        TODO("Not yet implemented")
    }
}