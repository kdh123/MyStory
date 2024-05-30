package com.dhkim.timecapsule.search.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dhkim.timecapsule.home.domain.Category
import com.dhkim.timecapsule.search.domain.Place
import kotlinx.coroutines.flow.Flow
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
}