package com.dhkim.location.domain

import androidx.paging.PagingData
import com.dhkim.common.CommonResult
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getNearPlaceByKeyword(query: String, lat: String, lng: String): Flow<PagingData<Place>>
    suspend fun getPlaceByKeyword(query: String): Flow<PagingData<Place>>
    suspend fun getPlaceByCategory(category: Category, lat: String, lng: String): Flow<PagingData<Place>>
    suspend fun getAddress(lat: String, lng: String): CommonResult<Address>
}