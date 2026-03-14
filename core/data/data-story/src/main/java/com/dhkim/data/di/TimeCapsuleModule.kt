package com.dhkim.data.di


import com.dhkim.data.dataSource.local.TimeCapsuleLocalDataSource
import com.dhkim.data.dataSource.local.TimeCapsuleLocalDataSourceImpl
import com.dhkim.data.repository.TimeCapsuleRepositoryImpl
import com.dhkim.domain.repository.TimeCapsuleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TimeCapsuleModule {

    @Binds
    @Singleton
    abstract fun bindTimeCapsuleRepository(timeCapsuleRepositoryImpl: TimeCapsuleRepositoryImpl): TimeCapsuleRepository

    @Binds
    @Singleton
    abstract fun bindTimeCapsuleLocalDataSource(timeCapsuleLocalDataSourceImpl: TimeCapsuleLocalDataSourceImpl): TimeCapsuleLocalDataSource
}