package com.dhkim.timecapsule.common.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dhkim.timecapsule.timecapsule.data.MyTimeCapsuleDao
import com.dhkim.timecapsule.timecapsule.data.MyTimeCapsuleEntity
import com.dhkim.timecapsule.timecapsule.data.ReceivedTimeCapsuleDao
import com.dhkim.timecapsule.timecapsule.data.ReceivedTimeCapsuleEntity
import com.dhkim.timecapsule.timecapsule.data.SendTimeCapsuleDao
import com.dhkim.timecapsule.timecapsule.data.SendTimeCapsuleEntity

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