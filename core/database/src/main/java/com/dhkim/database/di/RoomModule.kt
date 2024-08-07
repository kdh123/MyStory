package com.dhkim.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    fun roomBuilder(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "timeCapsule"
        ).addMigrations(MIGRATION_1_2)
            .build()
    }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        val SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS 'trip' (" +
                "'id' TEXT PRIMARY KEY NOT NULL default '', " +
                "'startDate' TEXT NOT NULL default '', " +
                "'endDate' TEXT NOT NULL default '', " +
                "'places' TEXT NOT NULL default '', " +
                "'images' TEXT NOT NULL default '', " +
                "'videos' TEXT NOT NULL default '')"

        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(SQL_CREATE_TABLE)
        }
    }
}