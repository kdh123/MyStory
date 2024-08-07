package com.dhkim.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dhkim.database.TripImageDto
import com.dhkim.database.TripVideoDto

@Entity(tableName = "trip")
data class TripEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "startData") val startDate: String,
    @ColumnInfo(name = "endDate") val endDate: String,
    @ColumnInfo(name = "places") val places: List<String>,
    @ColumnInfo(name = "images") val images: List<TripImageDto>,
    @ColumnInfo(name = "videos") val videos: List<TripVideoDto>
)
