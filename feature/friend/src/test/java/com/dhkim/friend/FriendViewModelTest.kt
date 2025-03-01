package com.dhkim.friend

import com.dhkim.MainDispatcherRule
import com.dhkim.testing.FakeUserRepository
import com.dhkim.user.domain.usecase.AcceptFriendUseCase
import com.dhkim.user.domain.usecase.AddFriendUseCase
import com.dhkim.user.domain.usecase.CreateFriendCodeUseCase
import com.dhkim.user.domain.usecase.DeleteFriendUseCase
import com.dhkim.user.domain.usecase.GetMyInfoUseCase
import com.dhkim.user.domain.usecase.SearchFriendUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FriendViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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
            ioDispatcher = Dispatchers.IO
        )
    }

    @Test
    fun `UiState 테스트`() = runBlocking {
        viewModel.uiState.first()
        delay(100)
        assertEquals(viewModel.uiState.value.myInfo.friends.size, 10)
        assertEquals(viewModel.uiState.value.myInfo.requests.size, 0)
    }
}