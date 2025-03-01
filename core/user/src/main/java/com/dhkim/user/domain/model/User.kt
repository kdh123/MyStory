package com.dhkim.user.domain.model

typealias UserId = String
typealias Nickname = String

data class User(
    val id: String = "",
    val uuid: String = "",
    val profileImage: String = "0",
    val friends: List<Friend> = listOf(),
    val requests: List<Friend> = listOf()
)

data class Friend(
    val id: String = "",
    val nickname: String = id,
    val profileImage: String = "",
    val uuid: String = "",
    val isPending: Boolean = true
)
