package com.dhkim.user.data.dataSource

import com.dhkim.database.entity.FriendEntity
import com.dhkim.user.domain.LocalFriend

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