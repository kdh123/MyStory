package com.dhkim.user.data.di

import com.dhkim.user.data.repository.UserRepositoryImpl
import com.dhkim.user.data.datasource.UserLocalDataSource
import com.dhkim.user.data.datasource.UserLocalDataSourceImpl
import com.dhkim.user.data.datasource.UserRemoteDataSource
import com.dhkim.user.data.datasource.UserRemoteDataSourceImpl
import com.dhkim.user.domain.repository.UserRepository
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