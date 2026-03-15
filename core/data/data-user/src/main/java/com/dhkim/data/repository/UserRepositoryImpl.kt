package com.dhkim.data.repository

import com.dhkim.common.CommonResult
import com.dhkim.data.datasource.UserLocalDataSource
import com.dhkim.data.datasource.UserRemoteDataSource
import com.dhkim.data.datasource.isSuccessful
import com.dhkim.data.toLocalFriend
import com.dhkim.domain.model.Friend
import com.dhkim.domain.model.LocalFriend
import com.dhkim.domain.model.User
import com.dhkim.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override fun getFriend(id: String): LocalFriend? {
        return localDataSource.getFriend(id)?.toLocalFriend()
    }

    override fun updateFriend(friend: Friend): Flow<isSuccessful> {
        return flow {
            val myId = localDataSource.getUserId()
            emit(remoteDataSource.updateFriend(myId, friend).first())
        }
    }

    override fun deleteLocalFriend(id: String) {
        localDataSource.deleteFriend(id)
    }

    override fun getMyInfo(myId: String): Flow<User> {
        return remoteDataSource.getMyInfo(myId = myId)
    }

    override suspend fun getMyId(): String {
        return localDataSource.getUserId()
    }

    override fun updateUser(user: User): Flow<isSuccessful> {
        return remoteDataSource.updateUser(user = user)
    }

    override fun searchUser(userId: String): Flow<CommonResult<User?>> {
        return remoteDataSource.searchUser(userId)
    }

    override suspend fun addFriend(userId: String, userProfileImage: String): Flow<isSuccessful> {
        return remoteDataSource.addFriend(
            myId = getMyId(),
            myProfileImage = "${getProfileImage()}",
            myUuid = getMyUuid(),
            userId = userId,
            userProfileImage = userProfileImage
        )
    }

    override fun deleteFriend(userId: String): Flow<isSuccessful> {
        return flow {
            emit(remoteDataSource.deleteFriend(myId = getMyId(), userId = userId).first())
        }
    }

    override suspend fun addRequests(userId: String): Flow<isSuccessful> {
        return remoteDataSource.addRequest(
            myId = getMyId(),
            myProfileImage = "${getProfileImage()}",
            userId = userId
        )
    }

    override fun acceptFriend(userId: String, userProfileImage: String, userUuid: String): Flow<isSuccessful> {
        return flow {
            val myId = getMyId()
            val myProfileImage = getProfileImage()
            val myUuid = getMyUuid()

            val isSuccessful = remoteDataSource.acceptFriend(
                myId = myId,
                myProfileImage = "$myProfileImage",
                myUuid = myUuid,
                userId = userId,
                userProfileImage = userProfileImage,
                userUuid = userUuid
            ).first()

            emit(isSuccessful)
        }
    }

    override suspend fun updateUserId(userId: String) {
        localDataSource.updateUserId(userId)
    }

    override suspend fun getProfileImage(): Int {
        return localDataSource.getProfileImage()
    }

    override suspend fun updateProfileImage(profileImage: String) {
        localDataSource.updateProfileImage(profileImage)
    }

    override suspend fun getMyUuid(): String {
        return localDataSource.getUuid()
    }

    override suspend fun updateUuid(uuid: String) {
        localDataSource.updateUuid(uuid)
    }

    override suspend fun registerPush(uuid: String, fcmToken: String): isSuccessful {
        return remoteDataSource.registerPush(uuid, fcmToken) is CommonResult.Success
    }
}