package com.dhkim.location

import androidx.paging.testing.asSnapshot
import com.dhkim.location.domain.repository.LocationRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
class RealLocationRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var locationRepository: LocationRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `실제 장소 데이터 가져오기 테스트`() = runTest {
        val data = locationRepository.getPlaceByKeyword("맛집").asSnapshot()

        println(data)
    }
}