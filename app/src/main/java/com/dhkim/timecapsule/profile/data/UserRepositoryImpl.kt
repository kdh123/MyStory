package com.dhkim.timecapsule.profile.data

import com.dhkim.timecapsule.profile.data.dataSource.UserLocalDataSource
import com.dhkim.timecapsule.profile.data.dataSource.UserRemoteDataSource
import com.dhkim.timecapsule.profile.domain.User
import com.dhkim.timecapsule.profile.domain.UserRepository
import kotlinx.coroutines.flow.catch
import javax.inject.Inject


class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun getMyId(): String {
        return localDataSource.getUserId()
    }

    override suspend fun signUp(userId: String) {
        localDataSource.updateUserId(userId = userId)
    }

    override fun updateUser(user: User) {
        remoteDataSource.updateUser(user = user)
    }

    override suspend fun getFcmToken(): String {
        return localDataSource.getFcmToken()
    }

    override suspend fun updateLocalFcmToken(fcmToken: String) {
        localDataSource.updateFcmToken(fcmToken = fcmToken)
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
}