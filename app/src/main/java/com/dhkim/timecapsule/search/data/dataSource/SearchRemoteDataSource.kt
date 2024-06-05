package com.dhkim.timecapsule.search.data.dataSource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.home.domain.Category
import com.dhkim.timecapsule.search.domain.Address
import com.dhkim.timecapsule.search.domain.Place
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject

class SearchRemoteDataSource @Inject constructor(
    private val api: Retrofit
) {
    private val service = api.create(SearchApi::class.java)

    fun getPlaceByKeyword(query: String, lat: String, lng: String): Flow<PagingData<Place>> {
        return Pager(PagingConfig(pageSize = 15)) {
            SearchKeywordPagingSource(service, query, lat, lng)
        }.flow
    }

    fun getPlaceByCategory(category: Category, lat: String, lng: String): Flow<PagingData<Place>> {
        return Pager(PagingConfig(pageSize = 15)) {
            SearchCategoryPagingSource(service, category, lat, lng)
        }.flow
    }

    suspend fun getAddress(lat: String, lng: String): CommonResult<Address> {
        return try {
            val response = service.getAddress(lat = lat, lng = lng)
            if (response.isSuccessful) {
                CommonResult.Success(data = response.body()?.toAddress() ?: Address())
            } else {
                val e = response.errorBody().toString()
                CommonResult.Error(code = -1)
            }
        } catch (e: HttpException) {
            CommonResult.Error(e.code())
        } catch (e: Exception) {
            val a = e.message
            CommonResult.Error(-1)
        }
    }
}