package com.dhkim.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.DateUtil
import com.dhkim.home.domain.TimeCapsule
import com.dhkim.home.domain.TimeCapsuleRepository
import com.dhkim.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val timeCapsuleItems = combine(
        timeCapsuleRepository.getMyAllTimeCapsule(),
        timeCapsuleRepository.getReceivedAllTimeCapsule()
    ) { myTimeCapsules, receivedTimeCapsules ->
        val myId = userRepository.getMyId()
        val myProfileImage = "${userRepository.getProfileImage()}"
        val timeCapsules = myTimeCapsules.map {
            val sharedFriends = it.sharedFriends.map { userId ->
                userRepository.getFriend(userId)?.nickname ?: userId
            }
            it.toTimeCapsule(myId, myProfileImage, sharedFriends)
        } + receivedTimeCapsules.map {
            val nickname = userRepository.getFriend(it.sender)?.nickname ?: it.sender
            it.toTimeCapsule(nickname)
        }
        with(timeCapsules) {
            val items = getTimeCapsules(0, toOpenableTimeCapsules()) +
                    getTimeCapsules(1, toOpenedTimeCapsules()) +
                    getTimeCapsules(2, toUnOpenedMyTimeCapsules() + toUnOpenedReceivedTimeCapsules()) +
                    getTimeCapsules(3)

            items.ifEmpty {
                getTimeCapsules(-1) + getTimeCapsules(3)
            }
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf())

    val uiState = timeCapsuleItems.map {
        TimeCapsuleUiState(
            isLoading = false,
            isNothing = it.isEmpty(),
            timeCapsules = it.toImmutableList()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimeCapsuleUiState())

    private val _sideEffect = Channel<TimeCapsuleSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    private var spaceId = 100

    private fun getTimeCapsules(
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
                    add(TimeCapsuleItem(id = spaceId++, type = TimeCapsuleType.Line))
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
                    add(TimeCapsuleItem(id = spaceId++, type = TimeCapsuleType.Line))
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
                    add(TimeCapsuleItem(id = spaceId++, type = TimeCapsuleType.Line))
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
                    add(TimeCapsuleItem(id = spaceId++, type = TimeCapsuleType.Line))
                }
            }

            3 -> {
                timeCapsuleItems.value.run {
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
        }

        return items
    }

    fun deleteTimeCapsule(timeCapsuleId: String, isReceived: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            with(timeCapsuleRepository) {
                if (isReceived) {
                    deleteReceivedTimeCapsule(timeCapsuleId)
                } else {
                    val sharedFriends = getMyTimeCapsule(timeCapsuleId)?.sharedFriends ?: listOf()
                    val sharedFriendsUuids = userRepository.getMyInfo().catch { }
                        .firstOrNull()?.friends
                        ?.filter {
                            sharedFriends.contains(it.id)
                        }?.map {
                            it.uuid
                        } ?: listOf()

                    if (sharedFriendsUuids.isNotEmpty()) {
                        val isSuccessful = deleteTimeCapsule(sharedFriendsUuids, timeCapsuleId)
                        if (isSuccessful) {
                            deleteMyTimeCapsule(timeCapsuleId)
                        } else {
                            _sideEffect.send(TimeCapsuleSideEffect.Message("삭제에 실패하였습니다."))
                        }
                    } else {
                        deleteMyTimeCapsule(timeCapsuleId)
                    }
                }
            }
        }
    }
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