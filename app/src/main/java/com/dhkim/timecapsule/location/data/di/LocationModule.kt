package com.dhkim.timecapsule.location.data.di

import com.dhkim.timecapsule.location.data.repository.LocationRepositoryImpl
import com.dhkim.timecapsule.location.domain.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository
}