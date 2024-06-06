package com.dhkim.timecapsule.profile.data

import com.dhkim.timecapsule.profile.data.dataSource.UserRemoteDataSource
import com.dhkim.timecapsule.profile.domain.User
import com.dhkim.timecapsule.profile.domain.UserRepository
import javax.inject.Inject


class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override fun updateUser(user: User) {
        remoteDataSource.updateUser(user = user)
    }
}