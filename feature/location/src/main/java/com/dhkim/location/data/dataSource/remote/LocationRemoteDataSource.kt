package com.dhkim.location.data.dataSource.remote

import androidx.paging.PagingData
import com.dhkim.common.CommonResult
import com.dhkim.location.domain.Address
import com.dhkim.location.domain.Category
import com.dhkim.location.domain.Place
import kotlinx.coroutines.flow.Flow

interface LocationRemoteDataSource {

    fun getNearPlaceByKeyword(query: String, lat: String, lng: String): Flow<PagingData<Place>>
    fun getPlaceByKeyword(query: String): Flow<PagingData<Place>>
    fun getPlaceByCategory(category: Category, lat: String, lng: String): Flow<PagingData<Place>>
    suspend fun getAddress(lat: String, lng: String): CommonResult<Address>
}