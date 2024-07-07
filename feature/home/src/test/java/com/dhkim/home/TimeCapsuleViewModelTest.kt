package com.dhkim.home

import com.dhkim.common.StableList
import com.dhkim.home.data.FakeTimeCapsuleLocalDataSource
import com.dhkim.home.data.FakeTimeCapsuleRepository
import com.dhkim.home.data.FakeUserLocalDataSource
import com.dhkim.home.data.FakeUserRemoteDataSource
import com.dhkim.home.data.dataSource.local.TimeCapsuleLocalDataSource
import com.dhkim.home.data.di.TimeCapsuleModule
import com.dhkim.home.domain.TimeCapsule
import com.dhkim.home.domain.TimeCapsuleRepository
import com.dhkim.home.presentation.TimeCapsuleType
import com.dhkim.home.presentation.TimeCapsuleViewModel
import com.dhkim.user.data.UserRepositoryImpl
import com.dhkim.user.data.dataSource.UserLocalDataSource
import com.dhkim.user.data.dataSource.UserLocalDataSourceImpl
import com.dhkim.user.data.dataSource.UserRemoteDataSource
import com.dhkim.user.data.dataSource.UserRemoteDataSourceImpl
import com.dhkim.user.data.di.UserModule
import com.dhkim.user.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
@UninstallModules(TimeCapsuleModule::class, UserModule::class)
class TimeCapsuleViewModelTest {

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

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class UserModule {

        @Binds
        @Singleton
        abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

        @Binds
        @Singleton
        abstract fun bindUserRemoteDataSource(fakeUserRemoteDataSource: FakeUserRemoteDataSource): UserRemoteDataSource

        @Binds
        @Singleton
        abstract fun bindUserLocalDataSource(fakeUserLocalDataSource: FakeUserLocalDataSource): UserLocalDataSource
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Inject lateinit var timeCapsuleRepository: TimeCapsuleRepository
    @Inject lateinit var userRepository: UserRepository

    private lateinit var viewModel: TimeCapsuleViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = TimeCapsuleViewModel(timeCapsuleRepository, userRepository)
    }

    @Test
    fun `상태 테스트`() = runBlocking {
        delay(500L)
        val timeCapsules = viewModel.uiState.value.timeCapsules
        val openableTimeCapsules = timeCapsules
            .firstOrNull { it.type == TimeCapsuleType.OpenableTimeCapsule }
            ?.data as? StableList<TimeCapsule> ?: StableList()

        val openedTimeCapsules = timeCapsules
            .firstOrNull { it.type == TimeCapsuleType.OpenedTimeCapsule }
            ?.data as? StableList<TimeCapsule> ?: StableList()

        val unopenedTimeCapsules = timeCapsules
            .firstOrNull { it.type == TimeCapsuleType.UnopenedTimeCapsule }
            ?.data as? StableList<TimeCapsule> ?: StableList()

        assertEquals(timeCapsules.size, 11)
        assertEquals(openableTimeCapsules.data.size, 2)
        assertEquals(openableTimeCapsules.data[0].id, "id2")

        assertEquals(openedTimeCapsules.data.size, 3)
        assertEquals(openedTimeCapsules.data[0].id, "id0")

        assertEquals(unopenedTimeCapsules.data.size, 15)
        assertEquals(unopenedTimeCapsules.data[0].id, "id1")
    }
}