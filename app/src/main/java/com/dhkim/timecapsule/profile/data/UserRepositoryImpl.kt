package com.dhkim.timecapsule.profile.data

import com.dhkim.timecapsule.profile.data.dataSource.UserLocalDataSource
import com.dhkim.timecapsule.profile.data.dataSource.UserRemoteDataSource
import com.dhkim.timecapsule.profile.domain.User
import com.dhkim.timecapsule.profile.domain.UserRepository
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


}