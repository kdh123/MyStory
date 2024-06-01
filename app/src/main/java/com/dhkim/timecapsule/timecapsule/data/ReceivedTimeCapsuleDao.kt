package com.dhkim.timecapsule.timecapsule.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceivedTimeCapsuleDao {

    @Query("SELECT * FROM receivedTimeCapsule")
    fun getAllTimeCapsule(): Flow<List<ReceivedTimeCapsuleEntity>?>

    @Query("SELECT * FROM receivedTimeCapsule WHERE id = :id")
    fun getTimeCapsule(id: String): ReceivedTimeCapsuleEntity?

    @Query("SELECT * FROM receivedTimeCapsule WHERE date = :startDate OR date = :endDate")
    fun getTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<ReceivedTimeCapsuleEntity>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTimeCapsule(timeCapsule: ReceivedTimeCapsuleEntity)

    @Update(entity = ReceivedTimeCapsuleEntity::class)
    fun updateTimeCapsule(timeCapsule: ReceivedTimeCapsuleEntity)

    @Query("DELETE FROM receivedTimeCapsule WHERE id = :id")
    fun deleteTimeCapsule(id: String)
}