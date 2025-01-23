package com.dhkim.story.data.di


import com.dhkim.story.data.dataSource.local.TimeCapsuleLocalDataSource
import com.dhkim.story.data.dataSource.local.TimeCapsuleLocalDataSourceImpl
import com.dhkim.story.data.repository.TimeCapsuleRepositoryImpl
import com.dhkim.story.domain.repository.TimeCapsuleRepository
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