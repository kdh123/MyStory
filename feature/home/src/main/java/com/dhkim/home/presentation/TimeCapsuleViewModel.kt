package com.dhkim.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.DateUtil
import com.dhkim.common.Dispatcher
import com.dhkim.common.RestartableStateFlow
import com.dhkim.common.TimeCapsuleDispatchers
import com.dhkim.common.onetimeRestartableStateIn
import com.dhkim.story.domain.model.TimeCapsule
import com.dhkim.story.domain.usecase.DeleteTimeCapsuleUseCase
import com.dhkim.story.domain.usecase.GetAllTimeCapsuleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleViewModel @Inject constructor(
    private val getAllTimeCapsuleUseCase: GetAllTimeCapsuleUseCase,
    private val deleteTimeCapsuleUseCase: DeleteTimeCapsuleUseCase,
    @Dispatcher(TimeCapsuleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val uiState: RestartableStateFlow<TimeCapsuleUiState> = getAllTimeCapsuleUseCase()
        .map { it.toUiState(spaceId = 100) }
        .onetimeRestartableStateIn(
            scope = viewModelScope,
            initialValue = TimeCapsuleUiState(),
            isOnetime = false
        )

    private val _sideEffect = Channel<TimeCapsuleSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun deleteTimeCapsule(timeCapsuleId: String, isReceived: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            val isSuccessful = deleteTimeCapsuleUseCase(timeCapsuleId, isReceived)
            if (!isSuccessful) {
                _sideEffect.send(TimeCapsuleSideEffect.Message("삭제에 실패하였습니다."))
            }
        }
    }
}

fun List<TimeCapsule>.toUiState(spaceId: Int): TimeCapsuleUiState {
    val items = getTimeCapsules(spaceId = spaceId, type = 0, timeCapsules = toOpenableTimeCapsules()) +
            getTimeCapsules(spaceId = spaceId + 1, type = 1, timeCapsules = toOpenedTimeCapsules()) +
            getTimeCapsules(
                spaceId = spaceId + 2,
                type = 2,
                timeCapsules = toUnOpenedMyTimeCapsules() + toUnOpenedReceivedTimeCapsules()
            )

    val result = items.ifEmpty { getTimeCapsules(spaceId + 3, -1) } + getTimeCapsules(spaceId + 4, 3)

    return TimeCapsuleUiState(
        isLoading = false,
        timeCapsules = result.toImmutableList()
    )
}

private fun getTimeCapsules(
    spaceId: Int,
    type: Int,
    timeCapsules: List<TimeCapsule> = listOf()
): List<TimeCapsuleItem> {
    val items = mutableListOf<TimeCapsuleItem>()
    when (type) {
        -1 -> {
            items.run {
                add(
                    TimeCapsuleItem(
                        id = 8,
                        type = TimeCapsuleType.Title,
                        "첫 타임캡슐을 만들어보세요"
                    )
                )
                add(TimeCapsuleItem(id = 9, type = TimeCapsuleType.NoneTimeCapsule))
                add(TimeCapsuleItem(id = spaceId, type = TimeCapsuleType.Line))
            }
        }

        0 -> {
            if (timeCapsules.isEmpty()) {
                return emptyList()
            }
            items.run {
                add(TimeCapsuleItem(id = 0, type = TimeCapsuleType.Title, "오늘 개봉할 수 있는 타임캡슐"))
                add(
                    TimeCapsuleItem(
                        id = 1,
                        type = TimeCapsuleType.OpenableTimeCapsule,
                        data = timeCapsules
                    )
                )
                add(TimeCapsuleItem(id = spaceId, type = TimeCapsuleType.Line))
            }
        }

        1 -> {
            if (timeCapsules.isEmpty()) {
                return emptyList()
            }
            items.run {
                add(TimeCapsuleItem(id = 6, type = TimeCapsuleType.Title, "나의 이야기"))
                add(
                    TimeCapsuleItem(
                        id = 7,
                        type = TimeCapsuleType.OpenedTimeCapsule,
                        data = timeCapsules.run {
                            if (size > 10) {
                                subList(0, 10)
                            } else {
                                this
                            }
                        }
                    )
                )
                add(TimeCapsuleItem(id = spaceId, type = TimeCapsuleType.Line))
            }
        }

        2 -> {
            if (timeCapsules.isEmpty()) {
                return emptyList()
            }
            items.run {
                add(TimeCapsuleItem(id = 2, type = TimeCapsuleType.Title, "미개봉 타임캡슐"))
                add(
                    TimeCapsuleItem(
                        id = 3,
                        type = TimeCapsuleType.UnopenedTimeCapsule,
                        data = timeCapsules
                    )
                )
                add(TimeCapsuleItem(id = spaceId, type = TimeCapsuleType.Line))
            }
        }

        3 -> {
            items.run {
                add(
                    TimeCapsuleItem(
                        id = 10,
                        type = TimeCapsuleType.Title,
                        data = "Tips"
                    )
                )
                add(
                    TimeCapsuleItem(
                        id = 11,
                        type = TimeCapsuleType.InviteFriend
                    )
                )
            }
        }
    }

    return items
}


fun List<TimeCapsule>.toUnOpenedMyTimeCapsules() =
    filter { !it.isReceived && !it.isOpened && !DateUtil.isAfter(it.openDate) }
        .sortedBy {
            it.openDate
        }

fun List<TimeCapsule>.toUnOpenedReceivedTimeCapsules() =
    filter { it.isReceived && !it.isOpened && !DateUtil.isAfter(it.openDate) }
        .sortedBy {
            it.openDate
        }

fun List<TimeCapsule>.toOpenableTimeCapsules() =
    filter { !it.isOpened && DateUtil.isAfter(strDate = it.openDate) }
        .sortedBy {
            it.openDate
        }

fun List<TimeCapsule>.toOpenedTimeCapsules() =
    filter { it.isOpened }
        .sortedByDescending {
            it.date
        }