package com.dhkim.data.di

import com.dhkim.data.datasource.UserLocalDataSource
import com.dhkim.data.datasource.UserLocalDataSourceImpl
import com.dhkim.data.datasource.UserRemoteDataSource
import com.dhkim.data.datasource.UserRemoteDataSourceImpl
import com.dhkim.data.repository.UserRepositoryImpl
import com.dhkim.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UserModule {

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