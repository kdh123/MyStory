package com.dhkim.location.data.dataSource.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dhkim.common.CommonResult
import com.dhkim.location.domain.model.Address
import com.dhkim.location.domain.model.Category
import com.dhkim.location.domain.model.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

internal class LocationRemoteDataSourceImpl @Inject constructor(
    private val service: LocationApi
) : LocationRemoteDataSource {

    override fun getNearPlaceByKeyword(query: String, lat: String, lng: String): Flow<PagingData<Place>> {
        return Pager(PagingConfig(pageSize = 15)) {
            SearchPlaceByKeywordPagingSource(
                api = service,
                query = query,
                lat = lat,
                lng = lng,
                isNear = true
            )
        }.flow
    }

    override fun getPlaceByKeyword(query: String): Flow<PagingData<Place>> {
        return Pager(PagingConfig(pageSize = 15)) {
            SearchPlaceByKeywordPagingSource(api = service, query = query, isNear = false)
        }.flow
    }

    override fun getPlaceByCategory(category: Category, lat: String, lng: String): Flow<PagingData<Place>> {
        return Pager(PagingConfig(pageSize = 15)) {
            SearchPlaceByCategoryPagingSource(service, category, lat, lng)
        }.flow
    }

    override fun getAddress(lat: String, lng: String): Flow<CommonResult<Address>> {
        return flow {
            val response = service.getAddress(lat = lat, lng = lng)
            if (response.isSuccessful) {
                emit(CommonResult.Success(data = response.body()?.toAddress() ?: Address()))
            } else {
                val err = response.errorBody().toString()
                emit(CommonResult.Error(code = -1))
            }
        }.catch {
            if (it is HttpException) {
                emit(CommonResult.Error(code = it.code()))
            } else {
                emit(CommonResult.Error(code = -1))
            }
        }
    }
}