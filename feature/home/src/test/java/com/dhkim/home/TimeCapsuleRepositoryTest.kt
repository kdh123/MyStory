package com.dhkim.home

import com.dhkim.database.entity.MyTimeCapsuleEntity
import com.dhkim.home.data.dataSource.local.TimeCapsuleLocalDataSource
import com.dhkim.home.data.di.TimeCapsuleModule
import com.dhkim.home.domain.TimeCapsuleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import org.junit.After
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
        FakeTimeCapsuleData.close()
    }

    @Test
    fun addition_isCorrect() = runBlocking {

        val myTimeCapsule = MyTimeCapsuleEntity(
            id = "id1",
            date = "2024-07-07",
            openDate = "2024-09-18",
            lat = "0.0",
            lng = "0.0",
            placeName = "광화문",
            address = "서울시 어딘가",
            content = "안녕하세요",
            images = listOf(),
            videos = listOf(),
            checkLocation = false,
            isOpened = false,
            sharedFriends = listOf()
        )

        FakeTimeCapsuleData.setMyTimeCapsuleEntity(myTimeCapsule)

        val data = timeCapsuleRepository.getMyTimeCapsule("")

        println("data2 : $data")
    }
}