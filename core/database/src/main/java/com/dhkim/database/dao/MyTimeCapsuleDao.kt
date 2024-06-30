package com.dhkim.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MyTimeCapsuleDao {

    @Query("SELECT * FROM myTimeCapsule")
    fun getAllTimeCapsule(): Flow<List<com.dhkim.database.entity.MyTimeCapsuleEntity>?>

    @Query("SELECT * FROM myTimeCapsule WHERE id = :id")
    fun getTimeCapsule(id: String): com.dhkim.database.entity.MyTimeCapsuleEntity?

    @Query("SELECT * FROM myTimeCapsule WHERE date = :startDate OR date = :endDate")
    fun getTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<com.dhkim.database.entity.MyTimeCapsuleEntity>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTimeCapsule(timeCapsule: com.dhkim.database.entity.MyTimeCapsuleEntity)

    @Update(entity = com.dhkim.database.entity.MyTimeCapsuleEntity::class)
    fun updateTimeCapsule(timeCapsule: com.dhkim.database.entity.MyTimeCapsuleEntity)

    @Query("DELETE FROM myTimeCapsule WHERE id = :id")
    fun deleteTimeCapsule(id: String)
}