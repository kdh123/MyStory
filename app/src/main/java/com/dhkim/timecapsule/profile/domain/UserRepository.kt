package com.dhkim.timecapsule.profile.domain

interface UserRepository {

    suspend fun getMyId(): String
    suspend fun signUp(userId: String)
    fun updateUser(user: User)
}