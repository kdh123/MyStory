package com.dhkim.timecapsule.timecapsule.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SendTimeCapsuleDao {

    @Query("SELECT * FROM sendTimeCapsule")
    fun getAllTimeCapsule(): Flow<List<SendTimeCapsuleEntity>?>

    @Query("SELECT * FROM sendTimeCapsule WHERE id = :id")
    fun getTimeCapsule(id: String): SendTimeCapsuleEntity?

    @Query("SELECT * FROM sendTimeCapsule WHERE date = :startDate OR date = :endDate")
    fun getTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<SendTimeCapsuleEntity>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTimeCapsule(timeCapsule: SendTimeCapsuleEntity)

    @Update(entity = SendTimeCapsuleEntity::class)
    fun updateTimeCapsule(timeCapsule: SendTimeCapsuleEntity)

    @Query("DELETE FROM sendTimeCapsule WHERE id = :id")
    fun deleteTimeCapsule(id: String)
}