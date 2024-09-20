package com.dhkim.user.domain

import androidx.compose.runtime.Stable

typealias UserId = String
typealias Nickname = String

data class User(
    val id: String = "",
    val uuid: String = "",
    val profileImage: String = "",
    val friends: List<Friend> = listOf(),
    val requests: List<Friend> = listOf()
)

@Stable
data class Friend(
    val id: String = "",
    val nickname: String = id,
    val profileImage: String = "",
    val uuid: String = "",
    val isPending: Boolean = true
)
