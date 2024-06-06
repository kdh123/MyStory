package com.dhkim.timecapsule.profile.domain

interface UserRepository {

    fun updateUser(user: User)
}