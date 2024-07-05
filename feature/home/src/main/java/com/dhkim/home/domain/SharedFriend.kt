package com.dhkim.home.domain


data class SharedFriend(
    val isChecked: Boolean = false,
    val userId: String = "",
    val nickname: String = userId,
    val uuid: String = ""
)