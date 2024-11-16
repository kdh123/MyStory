package com.dhkim.home.data

import com.dhkim.database.entity.MyTimeCapsuleEntity
import com.dhkim.home.data.dataSource.local.TimeCapsuleLocalDataSource
import com.dhkim.home.data.dataSource.toMyTimeCapsule
import com.dhkim.home.data.di.TimeCapsuleModule
import com.dhkim.home.domain.repository.TimeCapsuleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
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
@UninstallModules(TimeCapsuleModule::class)
class TimeCapsuleRepositoryTest {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class FakeTimeCapsuleModule {

        @Binds
        @Singleton
        abstract fun bindTimeCapsuleRepository(fakeTimeCapsuleRepository: FakeTimeCapsuleRepository): TimeCapsuleRepository

        @Binds
        @Singleton
        abstract fun bindTimeCapsuleLocalDataSource(fakeTimeCapsuleLocalDataSource: FakeTimeCapsuleLocalDataSource): TimeCapsuleLocalDataSource
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var timeCapsuleRepository: TimeCapsuleRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun teardown() {
    }

    @Test
    fun `특정 타임캡슐 가져오기`() = runBlocking {
        val myTimeCapsule = timeCapsuleRepository.getMyTimeCapsule("id1")

        assertEquals(myTimeCapsule?.placeName, "광화문1")
    }

    @Test
    fun `특정 타임캡슐 업데이트`() = runBlocking {
        val myTimeCapsuleEntity = MyTimeCapsuleEntity(
            id = "id1",
            date = "2024-07-07",
            openDate = "2024-09-18",
            lat = "0.0",
            lng = "0.0",
            placeName = "업데이트 장소",
            address = "서울시 어딘가100",
            content = "안녕하세요100",
            images = listOf(),
            videos = listOf(),
            checkLocation = false,
            isOpened = false,
            sharedFriends = listOf()
        )

        timeCapsuleRepository.editMyTimeCapsule(myTimeCapsuleEntity.toMyTimeCapsule())
        val placeName = timeCapsuleRepository.getMyAllTimeCapsule().first().first { it.id == "id1" }.placeName

        assertEquals(placeName, "업데이트 장소")
    }

    @Test
    fun `특정 타임캡슐 저장`() = runBlocking {
        val myTimeCapsuleEntity = MyTimeCapsuleEntity(
            id = "id100",
            date = "2024-07-07",
            openDate = "2024-09-18",
            lat = "0.0",
            lng = "0.0",
            placeName = "업데이트 장소",
            address = "서울시 어딘가100",
            content = "안녕하세요100",
            images = listOf(),
            videos = listOf(),
            checkLocation = false,
            isOpened = false,
            sharedFriends = listOf()
        )

        timeCapsuleRepository.saveMyTimeCapsule(myTimeCapsuleEntity.toMyTimeCapsule())

        assertEquals(timeCapsuleRepository.getMyAllTimeCapsule().first().size, 11)
    }

    @Test
    fun `특정 타임캡슐 삭제하기`() = runBlocking {
        timeCapsuleRepository.deleteMyTimeCapsule(id = "id1")

        assertEquals(timeCapsuleRepository.getMyAllTimeCapsule().first().size, 9)
    }
}