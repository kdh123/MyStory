package com.dhkim.location.data.di

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
internal abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindLocationRemoteDataSource(locationRemoteDataSourceImpl: com.dhkim.location.data.dataSource.remote.LocationRemoteDataSourceImpl): com.dhkim.location.data.dataSource.remote.LocationRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
internal object LocationApiModule {

    @Provides
    @Singleton
    fun provideLocationApi(@com.dhkim.network.di.RetrofitModule.KakaoLocal retrofit: Retrofit): com.dhkim.location.data.dataSource.remote.LocationApi {
        return retrofit.create(com.dhkim.location.data.dataSource.remote.LocationApi::class.java)
    }
}
