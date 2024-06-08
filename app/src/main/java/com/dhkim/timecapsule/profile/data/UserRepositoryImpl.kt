package com.dhkim.timecapsule.profile.data

import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.profile.data.dataSource.UserLocalDataSource
import com.dhkim.timecapsule.profile.data.dataSource.UserRemoteDataSource
import com.dhkim.timecapsule.profile.data.dataSource.isSuccessful
import com.dhkim.timecapsule.profile.domain.User
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.dhkim.timecapsule.profile.domain.isExist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject


class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun getMyId(): String {
        return localDataSource.getUserId()
    }

    override suspend fun signUp(userId: String, fcmToken: String): isSuccessful {
        val registerResult = remoteDataSource.registerPush(fcmToken)

        return when (registerResult) {
            is CommonResult.Success -> {
                val isSuccessful = remoteDataSource.updateFcmToken(userId, fcmToken).catch { }.firstOrNull() ?: false
                if (isSuccessful) {
                    localDataSource.updateUserId(userId = userId)
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

    override suspend fun searchUser(userId: String): Flow<isExist?> {
        val isExist = try {
            val result = remoteDataSource.searchUser(userId).first()

            when (result) {
                is CommonResult.Success -> {
                    result.data
                }

                is CommonResult.Error -> {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
        return flowOf(isExist)
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

    override suspend fun registerPush(fcmToken: String): isSuccessful {
        return remoteDataSource.registerPush(fcmToken) is CommonResult.Success
    }
}