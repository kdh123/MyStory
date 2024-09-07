package com.dhkim.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dhkim.database.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Query("SELECT * FROM trip")
    fun getAllTrip(): Flow<List<TripEntity>?>

    @Query("SELECT * FROM trip WHERE id = :id")
    fun getTrip(id: String): Flow<TripEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTrip(tripEntity: TripEntity)

    @Update(entity = TripEntity::class)
    fun updateTrip(tripEntity: TripEntity)

    @Query("DELETE FROM trip WHERE id = :id")
    fun deleteTrip(id: String)
}