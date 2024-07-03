package com.dhkim.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dhkim.database.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {

    @Query("SELECT * FROM friend")
    fun getAllFriend(): Flow<List<FriendEntity>?>

    @Query("SELECT * FROM friend WHERE id = :id")
    fun getFriend(id: String): FriendEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveFriend(friendEntity: FriendEntity)

    @Update(entity = FriendEntity::class)
    fun updateFriend(friendEntity: FriendEntity)

    @Query("DELETE FROM friend WHERE id = :id")
    fun deleteFriend(id: String)
}