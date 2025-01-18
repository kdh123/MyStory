package com.dhkim.location

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import androidx.paging.testing.asSnapshot
import com.dhkim.location.data.dataSource.remote.LocationApi
import com.dhkim.location.data.dataSource.remote.LocationRemoteDataSource
import com.dhkim.location.data.dataSource.remote.LocationRemoteDataSourceImpl
import com.dhkim.location.data.dataSource.remote.SearchPlaceByKeywordPagingSource
import com.dhkim.location.data.di.LocationApiModule
import com.dhkim.location.data.di.LocationModule
import com.dhkim.location.data.model.PlaceDocument
import com.dhkim.location.data.repository.LocationRepositoryImpl
import com.dhkim.location.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject
import javax.inject.Singleton

@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
@UninstallModules(LocationModule::class, LocationApiModule::class)
class LocationRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var locationRepository: LocationRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class FakeTimeCapsuleModule {

        @Binds
        @Singleton
        abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

        /*@Binds
        @Singleton
        abstract fun bindLocationRemoteDataSource(fakeLocationRemoteDataSource: FakeLocationRemoteDataSource): LocationRemoteDataSource*/

        @Binds
        @Singleton
        abstract fun bindLocationRemoteDataSource(locationRemoteDataSource: LocationRemoteDataSourceImpl): LocationRemoteDataSource

        @Binds
        @Singleton
        abstract fun bindFakeLocationApi(fakeLocationApi: FakeLocationApi): LocationApi

        //아래와 같이 LocationRemoteDataSourceImpl(상용 데이터 소스)를 사용하면 실제 서버에서 데이터 가져옴
        /*@Binds
        @Singleton
        abstract fun bindLocationRemoteDataSource(locationRemoteDataSourceImpl: LocationRemoteDataSourceImpl): LocationRemoteDataSource*/
    }


    @Test
    fun `데이터 체크`() = runBlocking {
        val data = locationRepository.getPlaceByKeyword("롯데타워").asSnapshot()
        val documents = mutableListOf<PlaceDocument>().apply {
            repeat(15) {
                add(
                    PlaceDocument(
                        address_name = "서울시 강남구$it",
                        category_group_code = "code$it",
                        category_group_name = "group$it",
                        category_name = "categoryName$it",
                        distance = "$it",
                        id = "placeId$it",
                        phone = "010-1234-1234",
                        place_name = "장소$it",
                        place_url = "url$it",
                        road_address_name = "강남로$it",
                        x = "34.3455",
                        y = "123.4233"
                    )
                )
            }
        }.map {
            it.toPlace()
        }

        val isContain = documents.map {
            data.contains(it)
        }.firstOrNull { !it }

        assertEquals(isContain, null)
    }

    @Test
    fun `에러 테스트`() = runTest {
        val pagingSource = SearchPlaceByKeywordPagingSource(
            FakeLocationApi().apply {
                setReturnsError()
            },
            "대한민국",
            isNear = true
        )

        val pager = TestPager(
            config = PagingConfig(pageSize = 30),
            pagingSource = pagingSource
        )

        val result = pager.refresh()
        Assert.assertTrue(result is PagingSource.LoadResult.Error)
    }
}