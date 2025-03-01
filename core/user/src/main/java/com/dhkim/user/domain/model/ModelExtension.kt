package com.dhkim.user.domain.model

import com.dhkim.database.entity.FriendEntity

fun FriendEntity.toLocalFriend(): LocalFriend {
    return LocalFriend(
        id, nickname, profileImage, uuid
    )
}

fun LocalFriend.toEntity(): FriendEntity {
    return FriendEntity(
        id, nickname, profileImage, uuid
    )
}