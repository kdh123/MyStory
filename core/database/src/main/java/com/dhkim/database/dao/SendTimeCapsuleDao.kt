package com.dhkim.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SendTimeCapsuleDao {

    @Query("SELECT * FROM sendTimeCapsule")
    fun getAllTimeCapsule(): Flow<List<com.dhkim.database.entity.SendTimeCapsuleEntity>?>

    @Query("SELECT * FROM sendTimeCapsule WHERE id = :id")
    fun getTimeCapsule(id: String): com.dhkim.database.entity.SendTimeCapsuleEntity?

    @Query("SELECT * FROM sendTimeCapsule WHERE date = :startDate OR date = :endDate")
    fun getTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<com.dhkim.database.entity.SendTimeCapsuleEntity>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTimeCapsule(timeCapsule: com.dhkim.database.entity.SendTimeCapsuleEntity)

    @Update(entity = com.dhkim.database.entity.SendTimeCapsuleEntity::class)
    fun updateTimeCapsule(timeCapsule: com.dhkim.database.entity.SendTimeCapsuleEntity)

    @Query("DELETE FROM sendTimeCapsule WHERE id = :id")
    fun deleteTimeCapsule(id: String)
}