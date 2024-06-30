package com.dhkim.timecapsule.data.di

import com.dhkim.timecapsule.data.repository.TimeCapsuleRepositoryImpl
import com.dhkim.timecapsule.domain.TimeCapsuleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeCapsuleModule {

    @Binds
    @Singleton
    abstract fun bindTimeCapsuleRepository(timeCapsuleRepositoryImpl: TimeCapsuleRepositoryImpl): TimeCapsuleRepository
}