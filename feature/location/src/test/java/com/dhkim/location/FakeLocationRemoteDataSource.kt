package com.dhkim.location

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.testing.asPagingSourceFactory
import com.dhkim.common.CommonResult
import com.dhkim.location.data.dataSource.remote.LocationApi
import com.dhkim.location.data.dataSource.remote.LocationRemoteDataSource
import com.dhkim.location.data.dataSource.remote.SearchPlaceByKeywordPagingSource
import com.dhkim.location.domain.Address
import com.dhkim.location.domain.Category
import com.dhkim.location.domain.Place
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FakeLocationRemoteDataSource @Inject constructor(
    private val locationApi: LocationApi
) : LocationRemoteDataSource {

    private val items = mutableListOf<Place>().apply {
        repeat(30) {
            add(
                Place(
                    id = "id$it",
                    name = "name$it",
                    address = "address$it",
                    category = Category.entries[it % 10].name,
                    distance = "3.4",
                    phone = "010-1234-1234",
                    url = "url$it"
                )
            )
        }
    }

    private val pagingSourceFactory = items.asPagingSourceFactory()
    private val pagingSource = pagingSourceFactory()

    override fun getNearPlaceByKeyword(query: String, lat: String, lng: String): Flow<PagingData<Place>> {
        val pagingSource = SearchPlaceByKeywordPagingSource(
            api = locationApi,
            query = "롯데타워",
            isNear = true
        )

        return Pager(
            config = PagingConfig(pageSize = 10)
        ) {
            pagingSource
        }.flow
    }

    override fun getPlaceByKeyword(query: String): Flow<PagingData<Place>> {
        return Pager(
            config = PagingConfig(pageSize = 10)
        ) {
            pagingSource
        }.flow
    }

    override fun getPlaceByCategory(category: Category, lat: String, lng: String): Flow<PagingData<Place>> {
        return Pager(
            config = PagingConfig(pageSize = 10)
        ) {
            pagingSource
        }.flow
    }

    override suspend fun getAddress(lat: String, lng: String): CommonResult<Address> {
        return CommonResult.Success(data = Address("힐푸", "남주길 150"))
    }
}