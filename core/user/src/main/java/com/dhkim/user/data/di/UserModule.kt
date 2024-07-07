package com.dhkim.user.data.di

import com.dhkim.user.data.UserRepositoryImpl
import com.dhkim.user.data.dataSource.UserLocalDataSource
import com.dhkim.user.data.dataSource.UserLocalDataSourceImpl
import com.dhkim.user.data.dataSource.UserRemoteDataSource
import com.dhkim.user.data.dataSource.UserRemoteDataSourceImpl
import com.dhkim.user.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(userRemoteDataSourceImpl: UserRemoteDataSourceImpl): UserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserLocalDataSource(userLocalDataSourceImpl: UserLocalDataSourceImpl): UserLocalDataSource
}