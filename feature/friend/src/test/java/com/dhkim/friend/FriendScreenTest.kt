package com.dhkim.friend

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhkim.friend.presentation.FriendScreen
import com.dhkim.friend.presentation.FriendSideEffect
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
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
@UninstallModules(UserModule::class)
class FriendScreenTest {

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
    var composeTestRule = createComposeRule()

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var viewModel: FriendViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = FriendViewModel(userRepository = userRepository)
    }

    @Test
    fun `친구 화면 목록 테스트`() = runTest {
        composeTestRule.setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = FriendSideEffect.None)

            FriendScreen(
                uiState = uiState,
                sideEffect = sideEffect,
                onQuery = viewModel::onQuery,
                onSearchUser = viewModel::searchUser,
                onAddFriend = viewModel::addFriend,
                onAcceptFriend = viewModel::acceptFriend,
                onDeleteFriend = viewModel::deleteFriend,
                onAddTimeCapsule = {},
                onCreateCode = viewModel::createCode,
                onNavigateToChangeInfo = {},
                onBack = {}
            )
        }

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("id0"),
            300
        )

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("nickname1"),
            300
        )

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("nickname2"),
            300
        )
    }
}