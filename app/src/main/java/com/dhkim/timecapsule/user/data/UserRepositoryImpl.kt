package com.dhkim.timecapsule.user.data

import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.common.presentation.profileImage
import com.dhkim.timecapsule.user.data.dataSource.UserLocalDataSource
import com.dhkim.timecapsule.user.data.dataSource.UserRemoteDataSource
import com.dhkim.timecapsule.user.data.dataSource.isSuccessful
import com.dhkim.timecapsule.user.domain.User
import com.dhkim.timecapsule.user.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject


class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun getMyInfo(): Flow<User> {
        return remoteDataSource.getMyInfo(myId = localDataSource.getUserId())
    }

    override suspend fun getMyId(): String {
        return localDataSource.getUserId()
    }

    override suspend fun signUp(userId: String, profileImage: String, fcmToken: String): isSuccessful {
        val uuid = (0..10_000_000_000).random().toString()
        val registerResult = remoteDataSource.registerPush(uuid, fcmToken)

        return when (registerResult) {
            is CommonResult.Success -> {
                val user = User(id = userId, profileImage = profileImage, uuid = uuid)
                val isSuccessful = remoteDataSource.updateUser(user).catch { }.firstOrNull() ?: false

                if (isSuccessful) {
                    localDataSource.run {
                        updateUserId(userId = userId)
                        updateProfileImage(profileImage = profileImage)
                        updateUuid(uuid = uuid)
                        updateFcmToken(fcmToken = fcmToken)
                    }
                    true
                } else {
                    false
                }
            }

            is CommonResult.Error -> {
                false
            }
        }
    }

    override fun updateUser(user: User) {
        remoteDataSource.updateUser(user = user)
    }

    override suspend fun searchUser(userId: String): Flow<CommonResult<User?>> {
        return remoteDataSource.searchUser(userId)
    }

    override suspend fun addFriend(userId: String, userProfileImage: String): Flow<isSuccessful> {
        return remoteDataSource.addFriend(
            myId = getMyId(),
            myProfileImage = getProfileImage().profileImage(),
            myUuid = getMyUuid(),
            userId = userId,
            userProfileImage = userProfileImage
        )
    }

    override suspend fun deleteFriend(userId: String): Flow<isSuccessful> {
        return remoteDataSource.deleteFriend(myId = getMyId(), userId = userId)
    }

    override suspend fun addRequests(userId: String): Flow<isSuccessful> {
        return remoteDataSource.addRequest(
            myId = getMyId(),
            myProfileImage = getProfileImage().profileImage(),
            userId = userId
        )
    }

    override suspend fun acceptFriend(userId: String, userProfileImage: String, userUuid: String): Flow<isSuccessful> {
        val myId = getMyId()
        val myProfileImage = getProfileImage().profileImage()
        val myUuid = getMyUuid()

        return remoteDataSource.acceptFriend(
            myId = myId,
            myProfileImage = myProfileImage,
            myUuid = myUuid,
            userId = userId,
            userProfileImage = userProfileImage,
            userUuid = userUuid
        )
    }

    override suspend fun getFcmToken(): String {
        return localDataSource.getFcmToken()
    }

    override suspend fun updateLocalFcmToken(fcmToken: String) {
        localDataSource.updateFcmToken(fcmToken = fcmToken)
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

    override suspend fun updateRemoteFcmToken(fcmToken: String) {
        val userId = getMyId()
        remoteDataSource.updateFcmToken(userId, fcmToken).catch { }
            .collect { isSuccessful ->
                if (isSuccessful) {
                    updateLocalFcmToken(fcmToken)
                } else {
                    updateLocalFcmToken("")
                }
            }
    }

    override suspend fun registerPush(uuid: String, fcmToken: String): isSuccessful {
        return remoteDataSource.registerPush(uuid, fcmToken) is CommonResult.Success
    }
}