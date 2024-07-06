package com.dhkim.user.data

import com.dhkim.common.CommonResult
import com.dhkim.user.data.dataSource.UserLocalDataSource
import com.dhkim.user.data.dataSource.UserRemoteDataSource
import com.dhkim.user.data.dataSource.isSuccessful
import com.dhkim.user.data.dataSource.toEntity
import com.dhkim.user.data.dataSource.toLocalFriend
import com.dhkim.user.domain.LocalFriend
import com.dhkim.user.domain.User
import com.dhkim.user.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override fun getAllFriend(): Flow<List<LocalFriend>> {
        return localDataSource.getAllFriend().map { friends ->
            friends?.map {
                it.toLocalFriend()
            } ?: listOf()
        }
    }

    override fun getFriend(id: String): LocalFriend? {
        return localDataSource.getFriend(id)?.toLocalFriend()
    }

    override fun saveFriend(localFriend: LocalFriend) {
        localDataSource.saveFriend(localFriend.toEntity())
    }

    override fun updateFriend(localFriend: LocalFriend) {
        localDataSource.updateFriend(localFriend.toEntity())
    }

    override fun deleteLocalFriend(id: String) {
        localDataSource.deleteFriend(id)
    }

    override suspend fun getMyInfo(): Flow<User> {
        val myId = localDataSource.getUserId()

        return if (myId.isNotEmpty()) {
            remoteDataSource.getMyInfo(myId = localDataSource.getUserId())
        } else {
            flowOf(User())
        }
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
            myProfileImage = "${getProfileImage()}",
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
            myProfileImage = "${getProfileImage()}",
            userId = userId
        )
    }

    override suspend fun acceptFriend(userId: String, userProfileImage: String, userUuid: String): Flow<isSuccessful> {
        val myId = getMyId()
        val myProfileImage = getProfileImage()
        val myUuid = getMyUuid()

        return remoteDataSource.acceptFriend(
            myId = myId,
            myProfileImage = "$myProfileImage",
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