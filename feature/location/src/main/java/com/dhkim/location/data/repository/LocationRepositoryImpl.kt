package com.dhkim.location.data.repository

import androidx.paging.PagingData
import com.dhkim.common.CommonResult
import com.dhkim.location.data.dataSource.remote.LocationRemoteDataSource
import com.dhkim.location.domain.Address
import com.dhkim.location.domain.Category
import com.dhkim.location.domain.LocationRepository
import com.dhkim.location.domain.Place
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val remoteDataSource: LocationRemoteDataSource
) : LocationRepository {

    override suspend fun getNearPlaceByKeyword(query: String, lat: String, lng: String): Flow<PagingData<Place>> {
        return remoteDataSource.getNearPlaceByKeyword(query, lat, lng)
    }

    override suspend fun getPlaceByKeyword(query: String): Flow<PagingData<Place>> {
        return remoteDataSource.getPlaceByKeyword(query = query)
    }

    override suspend fun getPlaceByCategory(category: Category, lat: String, lng: String): Flow<PagingData<Place>> {
        return remoteDataSource.getPlaceByCategory(category, lat, lng)
    }

    override suspend fun getAddress(lat: String, lng: String): CommonResult<Address> {
        return remoteDataSource.getAddress(lat = lat, lng = lng)
    }
}