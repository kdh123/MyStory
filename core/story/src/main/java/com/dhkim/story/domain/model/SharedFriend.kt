package com.dhkim.story.domain.model

data class SharedFriend(
    val isChecked: Boolean = false,
    val userId: String = "",
    val nickname: String = userId,
    val uuid: String = ""
)