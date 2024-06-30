package com.dhkim.database.di

import android.content.Context
import androidx.room.Room
import com.dhkim.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun roomBuilder(@ApplicationContext context: Context): com.dhkim.database.AppDatabase {
        return Room.databaseBuilder(
            context,
            com.dhkim.database.AppDatabase::class.java, "timeCapsule"
        ).build()
    }
}