package com.dhkim.home.domain.usecase

import com.dhkim.common.DateUtil
import com.dhkim.home.domain.model.TimeCapsule
import com.dhkim.home.domain.model.TimeCapsuleItem
import com.dhkim.home.domain.repository.TimeCapsuleRepository
import com.dhkim.home.domain.model.TimeCapsuleType
import com.dhkim.user.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val userRepository: UserRepository
) {

    private var spaceId = 100

    operator fun invoke(): Flow<List<TimeCapsuleItem>> {
        return combine(
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
                        getTimeCapsules(
                            2,
                            toUnOpenedMyTimeCapsules() + toUnOpenedReceivedTimeCapsules()
                        )

                items.ifEmpty { getTimeCapsules(-1) } + getTimeCapsules(3)
            }
        }.flowOn(Dispatchers.IO)
    }

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