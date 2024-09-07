package com.dhkim.trip.data.di

import com.dhkim.trip.data.dataSource.local.TripLocalDataSource
import com.dhkim.trip.data.dataSource.local.TripLocalDataSourceImpl
import com.dhkim.trip.data.dataSource.local.TripRepositoryImpl
import com.dhkim.trip.domain.TripRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TripModule {

    @Binds
    @Singleton
    abstract fun bindTripRepository(tripRepositoryImpl: TripRepositoryImpl): TripRepository

    @Binds
    @Singleton
    abstract fun bindTripLocalDataSource(tripLocalDataSourceImpl: TripLocalDataSourceImpl): TripLocalDataSource
}