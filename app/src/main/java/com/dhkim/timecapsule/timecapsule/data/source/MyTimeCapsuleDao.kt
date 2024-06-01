package com.dhkim.timecapsule.timecapsule.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MyTimeCapsuleDao {

    @Query("SELECT * FROM myTimeCapsule")
    fun getAllTimeCapsule(): Flow<List<MyTimeCapsuleEntity>?>

    @Query("SELECT * FROM myTimeCapsule WHERE id = :id")
    fun getTimeCapsule(id: String): MyTimeCapsuleEntity?

    @Query("SELECT * FROM myTimeCapsule WHERE date = :startDate OR date = :endDate")
    fun getTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<MyTimeCapsuleEntity>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTimeCapsule(timeCapsule: MyTimeCapsuleEntity)

    @Update(entity = MyTimeCapsuleEntity::class)
    fun updateTimeCapsule(timeCapsule: MyTimeCapsuleEntity)

    @Query("DELETE FROM myTimeCapsule WHERE id = :id")
    fun deleteTimeCapsule(id: String)
}