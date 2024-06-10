package com.dhkim.timecapsule.search.data.repository

import androidx.paging.PagingData
import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.home.domain.Category
import com.dhkim.timecapsule.search.data.dataSource.SearchRemoteDataSource
import com.dhkim.timecapsule.search.domain.Address
import com.dhkim.timecapsule.search.domain.Place
import com.dhkim.timecapsule.search.domain.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val remoteDataSource: SearchRemoteDataSource
) : SearchRepository {

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