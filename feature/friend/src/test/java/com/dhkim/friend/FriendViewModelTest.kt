package com.dhkim.friend

import com.dhkim.MainDispatcherRule
import com.dhkim.friend.presentation.FriendViewModel
import com.dhkim.user.FakeFriendUserLocalDataSource
import com.dhkim.user.FakeFriendUserRemoteDataSource
import com.dhkim.user.data.UserRepositoryImpl
import com.dhkim.user.data.dataSource.UserLocalDataSource
import com.dhkim.user.data.dataSource.UserRemoteDataSource
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
@UninstallModules(UserModule::class)
class FriendViewModelTest {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class FakeUserModule {

        @Binds
        @Singleton
        abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

        @Binds
        @Singleton
        abstract fun bindUserLocalDataSource(fakeFriendUserLocalDataSource: FakeFriendUserLocalDataSource): UserLocalDataSource

        @Binds
        @Singleton
        abstract fun bindUserRemoteDataSource(fakeUserRemoteDataSource: FakeFriendUserRemoteDataSource): UserRemoteDataSource
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var viewModel: FriendViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = FriendViewModel(userRepository = userRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `UiState 테스트`() = runBlocking {
        delay(1000L)
        println("test : ${userRepository.getAllFriend().first()}")
        val uiState = viewModel.uiState.value

        println(uiState.myInfo.friends)
        delay(1000L)
        println(viewModel.uiState.value.myInfo.friends)
    }
}