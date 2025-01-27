package com.dhkim.friend

import com.dhkim.MainDispatcherRule
import com.dhkim.user.FakeFriendUserLocalDataSource
import com.dhkim.user.FakeFriendUserRemoteDataSource
import com.dhkim.user.data.repository.UserRepositoryImpl
import com.dhkim.user.data.datasource.UserLocalDataSource
import com.dhkim.user.data.datasource.UserRemoteDataSource
import com.dhkim.user.data.di.UserModule
import com.dhkim.user.domain.repository.UserRepository
import com.dhkim.user.domain.usecase.AcceptFriendUseCase
import com.dhkim.user.domain.usecase.AddFriendUseCase
import com.dhkim.user.domain.usecase.CreateFriendCodeUseCase
import com.dhkim.user.domain.usecase.DeleteFriendUseCase
import com.dhkim.user.domain.usecase.GetMyInfoUseCase
import com.dhkim.user.domain.usecase.SearchFriendUseCase
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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

    @Inject
    lateinit var deleteFriendUseCase: DeleteFriendUseCase

    @Inject
    lateinit var createFriendCodeUseCase: CreateFriendCodeUseCase

    @Inject
    lateinit var addFriendUseCase: AddFriendUseCase

    @Inject
    lateinit var acceptFriendUseCase: AcceptFriendUseCase

    @Inject
    lateinit var searchFriendUseCase: SearchFriendUseCase

    @Inject
    lateinit var getMyInfoUseCase: GetMyInfoUseCase

    private lateinit var viewModel: FriendViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = FriendViewModel(
            deleteFriendUseCase = deleteFriendUseCase,
            createFriendCodeUseCase = createFriendCodeUseCase,
            addFriendUseCase = addFriendUseCase,
            acceptFriendUseCase = acceptFriendUseCase,
            searchFriendUseCase = searchFriendUseCase,
            getMyInfoUseCase = getMyInfoUseCase,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `UiState 테스트`() = runBlocking {
        viewModel.uiState.first()
        delay(100)
        assertEquals(viewModel.uiState.value.myInfo.friends.size, 2)
        assertEquals(viewModel.uiState.value.myInfo.requests.size, 0)
    }
}