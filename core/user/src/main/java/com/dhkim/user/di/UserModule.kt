package com.dhkim.user.di

import com.dhkim.user.repository.UserRepositoryImpl
import com.dhkim.user.datasource.UserLocalDataSource
import com.dhkim.user.datasource.UserLocalDataSourceImpl
import com.dhkim.user.datasource.UserRemoteDataSource
import com.dhkim.user.datasource.UserRemoteDataSourceImpl
import com.dhkim.user.repository.UserRepository
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