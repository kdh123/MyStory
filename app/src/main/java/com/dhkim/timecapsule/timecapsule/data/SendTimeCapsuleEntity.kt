package com.dhkim.timecapsule.timecapsule.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dhkim.timecapsule.timecapsule.domain.SendTimeCapsule

@Entity(tableName = "sendTimeCapsule")
data class SendTimeCapsuleEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "openDate") val openDate: String,
    @ColumnInfo(name = "receiver") val receiver: String,
    @ColumnInfo(name = "lat") val lat: String,
    @ColumnInfo(name = "lng") val lng: String,
    @ColumnInfo(name = "address") val address: List<String>,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "checkLocation") val checkLocation: Boolean
) {
    fun toSenderTimeCapsule(): SendTimeCapsule {
        return SendTimeCapsule(
            id, date, openDate, receiver, lat, lng, address, content, checkLocation
        )
    }
}