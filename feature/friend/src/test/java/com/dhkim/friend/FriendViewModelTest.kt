package com.dhkim.friend

import com.dhkim.MainDispatcherRule
import com.dhkim.user.FakeFriendUserLocalDataSource
import com.dhkim.user.FakeFriendUserRemoteDataSource
import com.dhkim.user.repository.UserRepositoryImpl
import com.dhkim.user.datasource.UserLocalDataSource
import com.dhkim.user.datasource.UserRemoteDataSource
import com.dhkim.user.di.UserModule
import com.dhkim.user.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import junit.framework.TestCase.assertEquals
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
    val mainDispatcherRule = MainDispatcherRule()

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var viewModel: FriendViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = FriendViewModel(userRepository = userRepository)
    }

    @Test
    fun `UiState 테스트`() = runBlocking {
        viewModel.uiState.first()
        delay(100)
        assertEquals(viewModel.uiState.value.myInfo.friends.size, 2)
        assertEquals(viewModel.uiState.value.myInfo.requests.size, 0)
    }
}