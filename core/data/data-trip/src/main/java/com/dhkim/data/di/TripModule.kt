package com.dhkim.data.di

import com.dhkim.data.dataSource.local.TripLocalDataSource
import com.dhkim.data.dataSource.local.TripLocalDataSourceImpl
import com.dhkim.data.dataSource.local.TripRepositoryImpl
import com.dhkim.domain.repository.TripRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TripModule {

    @Binds
    @Singleton
    abstract fun bindTripRepository(tripRepositoryImpl: TripRepositoryImpl): TripRepository

    @Binds
    @Singleton
    abstract fun bindTripLocalDataSource(tripLocalDataSourceImpl: TripLocalDataSourceImpl): TripLocalDataSource
}