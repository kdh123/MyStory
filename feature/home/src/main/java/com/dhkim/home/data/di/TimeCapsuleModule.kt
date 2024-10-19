package com.dhkim.home.data.di

import com.dhkim.home.data.dataSource.local.TimeCapsuleLocalDataSource
import com.dhkim.home.data.dataSource.local.TimeCapsuleLocalDataSourceImpl
import com.dhkim.home.data.repository.TimeCapsuleRepositoryImpl
import com.dhkim.home.domain.repository.TimeCapsuleRepository
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

    @Binds
    @Singleton
    abstract fun bindTimeCapsuleLocalDataSource(timeCapsuleLocalDataSourceImpl: TimeCapsuleLocalDataSourceImpl): TimeCapsuleLocalDataSource
}