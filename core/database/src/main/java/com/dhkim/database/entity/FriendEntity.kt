package com.dhkim.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend")
data class FriendEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "nickname") val nickname: String,
    @ColumnInfo(name = "profileImage") val profileImage: String,
    @ColumnInfo(name = "uuid") val uuid: String
)
