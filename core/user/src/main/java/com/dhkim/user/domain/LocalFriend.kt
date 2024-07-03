package com.dhkim.user.domain

data class LocalFriend(
    val id: String = "",
    val nickname: String = "",
    val profileImage: String = "",
    val uuid: String = ""
) {
    fun toFriend(): Friend {
        return Friend(
            id = id,
            profileImage = profileImage,
            uuid = uuid,
            isPending = false
        )
    }
}
