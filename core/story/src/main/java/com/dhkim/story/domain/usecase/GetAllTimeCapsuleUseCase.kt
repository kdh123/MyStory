package com.dhkim.story.domain.usecase

import com.dhkim.common.Dispatcher
import com.dhkim.common.TimeCapsuleDispatchers
import com.dhkim.story.domain.model.TimeCapsule
import com.dhkim.story.domain.repository.TimeCapsuleRepository
import com.dhkim.user.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val userRepository: UserRepository,
    @Dispatcher(TimeCapsuleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<List<TimeCapsule>> {
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
            timeCapsules
        }.flowOn(ioDispatcher)
    }
}