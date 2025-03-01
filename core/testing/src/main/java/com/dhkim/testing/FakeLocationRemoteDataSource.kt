package com.dhkim.testing

import android.annotation.SuppressLint
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.testing.asPagingSourceFactory
import com.dhkim.common.CommonResult
import com.dhkim.location.data.dataSource.remote.LocationRemoteDataSource
import com.dhkim.location.domain.model.Address
import com.dhkim.location.domain.model.Category
import com.dhkim.location.domain.model.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocationRemoteDataSource : LocationRemoteDataSource {

    private val items = mutableListOf<Place>().apply {
        repeat(15) {
            add(
                Place(
                    address = "서울시 강남구$it",
                    category = "categoryName$it",
                    distance = "$it",
                    id = "placeId$it",
                    phone = "010-1234-1234",
                    name = if (it == 2) "롯데타워" else "장소$it",
                    url = "url$it",
                    lat = "34.3455",
                    lng = "123.4233"
                )
            )
        }
    }

    @SuppressLint("VisibleForTests")
    private val pagingSourceFactory = items.asPagingSourceFactory()
    private val pagingSource = pagingSourceFactory()

    override fun getNearPlaceByKeyword(query: String, lat: String, lng: String): Flow<PagingData<Place>> {
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

    override fun getAddress(lat: String, lng: String): Flow<CommonResult<Address>> {
        return flow {
            emit(CommonResult.Success(data = Address("힐푸", "남주길 150")))
        }
    }
}