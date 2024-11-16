package com.dhkim.home

import com.dhkim.common.CommonResult
import com.dhkim.user.datasource.isSuccessful
import com.dhkim.user.model.Friend
import com.dhkim.user.model.LocalFriend
import com.dhkim.user.model.User
import com.dhkim.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class FakeUserRepository : UserRepository {

    override fun getAllFriend(): Flow<List<LocalFriend>> {
        TODO("Not yet implemented")
    }

    override fun getFriend(id: String): LocalFriend? {
        TODO("Not yet implemented")
    }

    override fun saveFriend(localFriend: LocalFriend) {
        TODO("Not yet implemented")
    }

    override suspend fun updateFriend(friend: Friend) {
        TODO("Not yet implemented")
    }

    override fun deleteLocalFriend(id: String) {
        TODO("Not yet implemented")
    }

    override fun getMyInfo(): Flow<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyId(): String {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(userId: String, profileImage: String, fcmToken: String): isSuccessful {
        TODO("Not yet implemented")
    }

    override fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun searchUser(userId: String): Flow<CommonResult<User?>> {
        TODO("Not yet implemented")
    }

    override suspend fun addFriend(userId: String, userProfileImage: String): Flow<isSuccessful> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFriend(userId: String): Flow<isSuccessful> {
        TODO("Not yet implemented")
    }

    override suspend fun addRequests(userId: String): Flow<isSuccessful> {
        TODO("Not yet implemented")
    }

    override suspend fun acceptFriend(userId: String, userProfileImage: String, userUuid: String): Flow<isSuccessful> {
        TODO("Not yet implemented")
    }

    override suspend fun getFcmToken(): String {
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