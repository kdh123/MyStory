package com.dhkim.data

import com.dhkim.database.entity.FriendEntity
import com.dhkim.domain.model.LocalFriend

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