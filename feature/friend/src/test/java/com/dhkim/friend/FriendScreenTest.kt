package com.dhkim.friend

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhkim.testing.FakeUserRepository
import com.dhkim.user.domain.usecase.AcceptFriendUseCase
import com.dhkim.user.domain.usecase.AddFriendUseCase
import com.dhkim.user.domain.usecase.CreateFriendCodeUseCase
import com.dhkim.user.domain.usecase.DeleteFriendUseCase
import com.dhkim.user.domain.usecase.GetMyInfoUseCase
import com.dhkim.user.domain.usecase.SearchFriendUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class FriendScreenTest {

    @get:Rule
    var composeTestRule = createComposeRule()

    private val userRepository = FakeUserRepository()

    private val deleteFriendUseCase = DeleteFriendUseCase(userRepository)

    private val createFriendCodeUseCase = CreateFriendCodeUseCase(userRepository)

    private val addFriendUseCase = AddFriendUseCase(userRepository)

    private val acceptFriendUseCase = AcceptFriendUseCase(userRepository)

    private val searchFriendUseCase = SearchFriendUseCase(userRepository)

    private val getMyInfoUseCase = GetMyInfoUseCase(userRepository)

    private lateinit var viewModel: FriendViewModel

    @Before
    fun setup() {
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
    fun `친구 화면 목록 테스트`() = runTest {
        composeTestRule.setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect = viewModel.sideEffect

            FriendScreen(
                uiState = uiState,
                sideEffect = { sideEffect },
                onAction = viewModel::onAction,
                onNavigateToChangeInfo = {},
                onNavigateToAddTimeCapsule = {},
                showPopup = {},
                onBack = {}
            )
        }

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("nickname0"),
            100
        )

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("nickname1"),
            100
        )
    }
}