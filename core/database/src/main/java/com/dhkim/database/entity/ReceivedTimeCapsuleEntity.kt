package com.dhkim.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receivedTimeCapsule")
data class ReceivedTimeCapsuleEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "openDate") val openDate: String,
    @ColumnInfo(name = "sender") val sender: String,
    @ColumnInfo(name = "profileImage") val profileImage: String,
    @ColumnInfo(name = "lat") val lat: String,
    @ColumnInfo(name = "lng") val lng: String,
    @ColumnInfo(name = "placeName") val placeName: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "checkLocation") val checkLocation: Boolean,
    @ColumnInfo(name = "isOpened") val isOpened: Boolean
)