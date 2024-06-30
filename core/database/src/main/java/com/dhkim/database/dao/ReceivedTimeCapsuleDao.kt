package com.dhkim.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceivedTimeCapsuleDao {

    @Query("SELECT * FROM receivedTimeCapsule")
    fun getAllTimeCapsule(): Flow<List<com.dhkim.database.entity.ReceivedTimeCapsuleEntity>?>

    @Query("SELECT * FROM receivedTimeCapsule WHERE id = :id")
    fun getTimeCapsule(id: String): com.dhkim.database.entity.ReceivedTimeCapsuleEntity?

    @Query("SELECT * FROM receivedTimeCapsule WHERE date = :startDate OR date = :endDate")
    fun getTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<com.dhkim.database.entity.ReceivedTimeCapsuleEntity>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTimeCapsule(timeCapsule: com.dhkim.database.entity.ReceivedTimeCapsuleEntity)

    @Update(entity = com.dhkim.database.entity.ReceivedTimeCapsuleEntity::class)
    fun updateTimeCapsule(timeCapsule: com.dhkim.database.entity.ReceivedTimeCapsuleEntity)

    @Query("DELETE FROM receivedTimeCapsule WHERE id = :id")
    fun deleteTimeCapsule(id: String)
}