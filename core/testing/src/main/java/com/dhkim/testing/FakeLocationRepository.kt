package com.dhkim.testing

import androidx.paging.PagingData
import com.dhkim.common.CommonResult
import com.dhkim.location.data.dataSource.remote.LocationRemoteDataSource
import com.dhkim.location.domain.model.Address
import com.dhkim.location.domain.model.Category
import com.dhkim.location.domain.model.Place
import com.dhkim.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class FakeLocationRepository(
    private val remoteDataSource: LocationRemoteDataSource = FakeLocationRemoteDataSource()
) : LocationRepository {

    override fun getNearPlaceByKeyword(query: String, lat: String, lng: String): Flow<PagingData<Place>> {
        return remoteDataSource.getNearPlaceByKeyword(query, lat, lng)
    }

    override fun getPlaceByKeyword(query: String): Flow<PagingData<Place>> {
        return remoteDataSource.getPlaceByKeyword(query = query)
    }

    override fun getPlaceByCategory(category: Category, lat: String, lng: String): Flow<PagingData<Place>> {
        return remoteDataSource.getPlaceByCategory(category, lat, lng)
    }

    override fun getAddress(lat: String, lng: String): Flow<CommonResult<Address>> {
        return remoteDataSource.getAddress(lat = lat, lng = lng)
    }
}