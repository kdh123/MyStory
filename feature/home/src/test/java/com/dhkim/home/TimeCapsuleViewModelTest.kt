package com.dhkim.home

import com.dhkim.home.data.FakeTimeCapsuleLocalDataSource
import com.dhkim.home.data.FakeTimeCapsuleRepository
import com.dhkim.home.data.FakeUserLocalDataSource
import com.dhkim.home.data.FakeUserRemoteDataSource
import com.dhkim.home.presentation.TimeCapsuleType
import com.dhkim.home.presentation.TimeCapsuleViewModel
import com.dhkim.story.data.dataSource.local.TimeCapsuleLocalDataSource
import com.dhkim.story.data.di.TimeCapsuleModule
import com.dhkim.story.domain.model.TimeCapsule
import com.dhkim.story.domain.repository.TimeCapsuleRepository
import com.dhkim.story.domain.usecase.DeleteTimeCapsuleUseCase
import com.dhkim.story.domain.usecase.GetAllTimeCapsuleUseCase
import com.dhkim.user.datasource.UserLocalDataSource
import com.dhkim.user.datasource.UserRemoteDataSource
import com.dhkim.user.di.UserModule
import com.dhkim.user.repository.UserRepository
import com.dhkim.user.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
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
    @Inject lateinit var getAllTimeCapsuleUseCase: GetAllTimeCapsuleUseCase
    @Inject lateinit var deleteTimeCapsuleUseCase: DeleteTimeCapsuleUseCase


    private lateinit var viewModel: TimeCapsuleViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = TimeCapsuleViewModel(
            getAllTimeCapsuleUseCase = getAllTimeCapsuleUseCase,
            deleteTimeCapsuleUseCase = deleteTimeCapsuleUseCase,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `상태 테스트`() = runBlocking {
        viewModel.uiState.first()
        delay(100)
        val timeCapsules = viewModel.uiState.value.timeCapsules
        val openableTimeCapsules = timeCapsules
            .firstOrNull { it.type == TimeCapsuleType.OpenableTimeCapsule }
            ?.data as? List<TimeCapsule> ?: listOf()

        val openedTimeCapsules = timeCapsules
            .firstOrNull { it.type == TimeCapsuleType.OpenedTimeCapsule }
            ?.data as? List<TimeCapsule> ?: listOf()

        val unopenedTimeCapsules = timeCapsules
            .firstOrNull { it.type == TimeCapsuleType.UnopenedTimeCapsule }
            ?.data as? List<TimeCapsule> ?: listOf()


        assertEquals(openableTimeCapsules.size, 7)
        assertEquals(openableTimeCapsules[0].id, "id2")

        assertEquals(openedTimeCapsules.size, 3)
        assertEquals(openedTimeCapsules[0].id, "id0")

        assertEquals(unopenedTimeCapsules.size, 10)
        assertEquals(unopenedTimeCapsules[0].id, "id1")
    }

    @Test
    fun `삭제 테스트`() = runBlocking {
        viewModel.uiState.first()
        delay(100)
        viewModel.deleteTimeCapsule("receivedId1", isReceived = true)
        delay(100)

        val timeCapsules = viewModel.uiState.value.timeCapsules
        val unopenedTimeCapsules = timeCapsules
            .firstOrNull { it.type == TimeCapsuleType.UnopenedTimeCapsule }
            ?.data as? List<TimeCapsule> ?: listOf()

        assertEquals(unopenedTimeCapsules.size, 9)
    }
}