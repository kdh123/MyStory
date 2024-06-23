package com.dhkim.timecapsule.user.domain

typealias UserId = String

data class User(
    val id: String = "",
    val uuid: String = "",
    val profileImage: String = "",
    val friends: List<Friend> = listOf(),
    val requests: List<Friend> = listOf()
)

data class Friend(
    val id: String = "",
    val profileImage: String = "",
    val uuid: String = "",
    val isPending: Boolean = true
)
