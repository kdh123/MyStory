package com.dhkim.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import com.dhkim.home.data.FakeTimeCapsuleRepository
import com.dhkim.home.presentation.DefaultPermissionState
import com.dhkim.home.presentation.TimeCapsuleScreen
import com.dhkim.home.presentation.TimeCapsuleViewModel
import com.dhkim.story.domain.usecase.DeleteTimeCapsuleUseCase
import com.dhkim.story.domain.usecase.GetAllTimeCapsuleUseCase
import com.dhkim.testing.FakeUserRepository
import com.dhkim.user.domain.usecase.GetMyInfoUseCase
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class TimeCapsuleScreenTest {

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val composeRule = createComposeRule()

    private val timeCapsuleRepository = FakeTimeCapsuleRepository()
    private val userRepository = FakeUserRepository()
    private val getAllTimeCapsuleUseCase = GetAllTimeCapsuleUseCase(timeCapsuleRepository, userRepository, Dispatchers.IO)
    private val getMyInfoUseCase = GetMyInfoUseCase(userRepository)
    private val deleteTimeCapsuleUseCase = DeleteTimeCapsuleUseCase(timeCapsuleRepository, userRepository, getMyInfoUseCase)

    private lateinit var viewModel: TimeCapsuleViewModel

    @Before
    fun setup() {
        viewModel = TimeCapsuleViewModel(
            getAllTimeCapsuleUseCase = getAllTimeCapsuleUseCase,
            deleteTimeCapsuleUseCase = deleteTimeCapsuleUseCase,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Test
    fun `UI 테스트`() = runBlocking {
        // Start the app
        viewModel.uiState.first()
        delay(300L)
        val uiState = viewModel.uiState.value

        composeRule.setContent {
            TimeCapsuleScreen(
                uiState = uiState,
                sideEffect = { flowOf() },
                permissionState = DefaultPermissionState(),
                onDeleteTimeCapsule = { _, _ -> },
                onNavigateToAdd = {},
                onNavigateToOpen = { _, _ -> },
                onNavigateToDetail = { _, _ -> },
                onNavigateToNotification = {},
                onNavigateToSetting = {},
                onNavigateToProfile = {},
                onNavigateToMore = {},
                showPopup = {},
                requestPermission = {}
            )
        }

        composeRule.waitUntilExists(
            hasTestTag("openableTimeCapsules"),
            300L
        )

        composeRule.waitUntilExists(
            hasTestTag("lockTimeCapsuleid2"),
            300L
        )

        composeRule.onNodeWithTag("timeCapsuleItems").performScrollToIndex(6)

        composeRule.waitUntilExists(
            hasTestTag("lockTimeCapsuleid1"),
            300L
        )
    }

    @OptIn(ExperimentalTestApi::class)
    fun ComposeContentTestRule.waitUntilExists(
        matcher: SemanticsMatcher,
        timeoutMillis: Long = 1000L
    ) = waitUntilNodeCount(matcher, 1, timeoutMillis)
}