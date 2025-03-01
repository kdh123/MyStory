package com.dhkim.home

import com.dhkim.home.data.FakeTimeCapsuleRepository
import com.dhkim.home.presentation.TimeCapsuleType
import com.dhkim.home.presentation.TimeCapsuleViewModel
import com.dhkim.story.domain.model.TimeCapsule
import com.dhkim.story.domain.usecase.DeleteTimeCapsuleUseCase
import com.dhkim.story.domain.usecase.GetAllTimeCapsuleUseCase
import com.dhkim.user.domain.usecase.GetMyInfoUseCase
import kotlinx.coroutines.Dispatchers
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class TimeCapsuleViewModelTest {

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

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