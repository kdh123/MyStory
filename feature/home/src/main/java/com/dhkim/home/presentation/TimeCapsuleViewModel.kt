package com.dhkim.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.DateUtil
import com.dhkim.common.StableList
import com.dhkim.home.domain.TimeCapsuleRepository
import com.dhkim.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimeCapsuleUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<TimeCapsuleSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var spaceId = 100

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val myId = userRepository.getMyId()
            val myProfileImage = "${userRepository.getProfileImage()}"
            timeCapsuleRepository.getMyAllTimeCapsule()
                .combine(timeCapsuleRepository.getReceivedAllTimeCapsule()) { myTimeCapsules, receivedTimeCapsules ->
                    myTimeCapsules.map { it.toTimeCapsule(myId, myProfileImage) } + receivedTimeCapsules.map { it.toTimeCapsule() }
                }.catch { }
                .collect { timeCapsules ->
                    val timeCapsuleItems = mutableListOf<TimeCapsuleItem>()

                    val unOpenedMyTimeCapsules = timeCapsules
                        .filter { !it.isReceived && !it.isOpened && !DateUtil.isAfter(it.openDate) }
                        .sortedBy {
                            it.openDate
                        }
                    val unOpenedReceivedTimeCapsules = timeCapsules
                        .filter { it.isReceived && !it.isOpened && !DateUtil.isAfter(it.openDate) }
                        .sortedBy {
                            it.openDate
                        }
                    val openableTimeCapsules = timeCapsules
                        .filter { (!it.isOpened && DateUtil.isAfter(strDate = it.openDate)) }
                        .sortedBy {
                            it.openDate
                        }
                    val openedTimeCapsules = timeCapsules.filter { it.isOpened }
                        .sortedByDescending {
                            it.date
                        }

                    if (openableTimeCapsules.isNotEmpty()) {
                        timeCapsuleItems.run {
                            add(TimeCapsuleItem(id = 0, type = TimeCapsuleType.Title, "오늘 개봉할 수 있는 타임캡슐"))
                            add(
                                TimeCapsuleItem(
                                    id = 1,
                                    type = TimeCapsuleType.OpenableTimeCapsule,
                                    data = StableList(data = openableTimeCapsules)
                                )
                            )
                            add(TimeCapsuleItem(id = spaceId++, type = TimeCapsuleType.Line))
                        }
                    }

                    if (openedTimeCapsules.isNotEmpty()) {
                        timeCapsuleItems.run {
                            add(TimeCapsuleItem(id = 6, type = TimeCapsuleType.Title, "나의 이야기"))
                            add(
                                TimeCapsuleItem(
                                    id = 7,
                                    type = TimeCapsuleType.OpenedTimeCapsule,
                                    data = StableList(data = openedTimeCapsules)
                                )
                            )
                            add(TimeCapsuleItem(id = spaceId++, type = TimeCapsuleType.Line))
                        }
                    }

                    if (unOpenedMyTimeCapsules.isNotEmpty()) {
                        timeCapsuleItems.run {
                            add(TimeCapsuleItem(id = 2, type = TimeCapsuleType.Title, "미개봉 타임캡슐"))
                            add(
                                TimeCapsuleItem(
                                    id = 3,
                                    type = TimeCapsuleType.UnopenedTimeCapsule,
                                    data = StableList(data = unOpenedMyTimeCapsules + unOpenedReceivedTimeCapsules)
                                )
                            )
                            add(TimeCapsuleItem(id = spaceId++, type = TimeCapsuleType.Line))
                        }
                    }

                    if (timeCapsuleItems.isEmpty()) {
                        timeCapsuleItems.run {
                            add(TimeCapsuleItem(id = 8, type = TimeCapsuleType.Title, "나의 첫 타임캡슐을 만들어보세요."))
                            add(TimeCapsuleItem(id = 9, type = TimeCapsuleType.NoneTimeCapsule))
                        }
                    }

                    timeCapsuleItems.add(TimeCapsuleItem(id = 10, type = TimeCapsuleType.Title, data = "Tips"))
                    timeCapsuleItems.add(TimeCapsuleItem(id = 11, type = TimeCapsuleType.InviteFriend))

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isNothing = timeCapsules.isEmpty(),
                        timeCapsules = timeCapsuleItems
                    )
                }
        }
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
                            _sideEffect.emit(TimeCapsuleSideEffect.Message("삭제에 실패하였습니다."))
                        }
                    } else {
                        deleteMyTimeCapsule(timeCapsuleId)
                    }
                }
            }
        }
    }

    fun onAction(action: TimeCapsuleAction) {
        viewModelScope.launch(Dispatchers.IO) {
            when (action) {
                is TimeCapsuleAction.SaveMyTimeCapsule -> {
                    timeCapsuleRepository.saveMyTimeCapsule(timeCapsule = action.timeCapsule)
                }

                is TimeCapsuleAction.EditMyTimeCapsule -> {
                    timeCapsuleRepository.editMyTimeCapsule(timeCapsule = action.timeCapsule)
                }

                is TimeCapsuleAction.DeleteMyTimeCapsule -> {
                    timeCapsuleRepository.deleteMyTimeCapsule(id = action.id)
                }


                is TimeCapsuleAction.SaveSenderTimeCapsule -> {
                    timeCapsuleRepository.saveSendTimeCapsule(timeCapsule = action.timeCapsule)
                }

                is TimeCapsuleAction.EditSenderTimeCapsule -> {
                    timeCapsuleRepository.editSendTimeCapsule(timeCapsule = action.timeCapsule)
                }

                is TimeCapsuleAction.DeleteSenderTimeCapsule -> {
                    timeCapsuleRepository.deleteSendTimeCapsule(id = action.id)
                }


                is TimeCapsuleAction.SaveReceivedTimeCapsule -> {
                    timeCapsuleRepository.saveReceivedTimeCapsule(timeCapsule = action.timeCapsule)
                }

                is TimeCapsuleAction.DeleteReceivedTimeCapsule -> {
                    timeCapsuleRepository.deleteReceivedTimeCapsule(id = action.id)
                }
            }
        }
    }
}