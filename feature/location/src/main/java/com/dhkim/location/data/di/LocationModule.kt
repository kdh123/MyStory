package com.dhkim.location.data.di

import com.dhkim.location.data.dataSource.remote.LocationApi
import com.dhkim.location.data.dataSource.remote.LocationRemoteDataSource
import com.dhkim.location.data.dataSource.remote.LocationRemoteDataSourceImpl
import com.dhkim.location.data.repository.LocationRepositoryImpl
import com.dhkim.location.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindLocationRemoteDataSource(locationRemoteDataSourceImpl: LocationRemoteDataSourceImpl): LocationRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object LocationApiModule {

    @Provides
    @Singleton
    fun provideLocationApi(@com.dhkim.network.di.RetrofitModule.KakaoLocal retrofit: Retrofit): LocationApi {
        return retrofit.create(LocationApi::class.java)
    }
}
