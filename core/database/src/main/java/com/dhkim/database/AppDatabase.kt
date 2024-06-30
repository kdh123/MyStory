package com.dhkim.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dhkim.database.dao.MyTimeCapsuleDao
import com.dhkim.database.dao.ReceivedTimeCapsuleDao
import com.dhkim.database.dao.SendTimeCapsuleDao
import com.dhkim.database.entity.MyTimeCapsuleEntity
import com.dhkim.database.entity.ReceivedTimeCapsuleEntity
import com.dhkim.database.entity.SendTimeCapsuleEntity


@Database(
    entities = [MyTimeCapsuleEntity::class, SendTimeCapsuleEntity::class, ReceivedTimeCapsuleEntity::class],
    version = 1
)
@TypeConverters(RoomConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun myTimeCapsuleDao(): MyTimeCapsuleDao
    abstract fun sendTimeCapsuleDao(): SendTimeCapsuleDao
    abstract fun receivedTimeCapsuleDao(): ReceivedTimeCapsuleDao
}