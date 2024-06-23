package com.dhkim.timecapsule.location.data.repository

import androidx.paging.PagingData
import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.location.domain.Category
import com.dhkim.timecapsule.location.data.dataSource.remote.LocationRemoteDataSource
import com.dhkim.timecapsule.location.domain.Address
import com.dhkim.timecapsule.location.domain.Place
import com.dhkim.timecapsule.location.domain.LocationRepository
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