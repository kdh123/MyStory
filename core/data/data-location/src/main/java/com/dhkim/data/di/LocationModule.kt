package com.dhkim.data.di

import com.dhkim.data.dataSource.remote.LocationApi
import com.dhkim.data.dataSource.remote.LocationRemoteDataSource
import com.dhkim.data.dataSource.remote.LocationRemoteDataSourceImpl
import com.dhkim.data.repository.LocationRepositoryImpl
import com.dhkim.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindLocationRemoteDataSource(locationRemoteDataSourceImpl: LocationRemoteDataSourceImpl): LocationRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
internal object LocationApiModule {

    @Provides
    @Singleton
    fun provideLocationApi(@com.dhkim.network.di.RetrofitModule.KakaoLocal retrofit: Retrofit): LocationApi {
        return retrofit.create(LocationApi::class.java)
    }
}
