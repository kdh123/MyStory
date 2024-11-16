package com.dhkim.home.domain.usecase

import com.dhkim.home.domain.repository.TimeCapsuleRepository
import com.dhkim.home.domain.repository.isSuccessful
import com.dhkim.user.repository.UserRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class DeleteTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(timeCapsuleId: String, isReceived: Boolean): isSuccessful {
        with(timeCapsuleRepository) {
            return if (isReceived) {
                deleteReceivedTimeCapsule(timeCapsuleId)
                true
            } else {
                val sharedFriends = getMyTimeCapsule(timeCapsuleId)?.sharedFriends ?: listOf()
                val myId = userRepository.getMyId()
                val sharedFriendsUuids = userRepository.getMyInfo().catch { }
                    .firstOrNull()?.friends
                    ?.filter {
                        sharedFriends.contains(it.id)
                    }?.map {
                        it.uuid
                    } ?: listOf()

                if (sharedFriendsUuids.isNotEmpty()) {
                    val isSuccessful = deleteTimeCapsule(myId, sharedFriendsUuids, timeCapsuleId)
                    if (isSuccessful) {
                        deleteMyTimeCapsule(timeCapsuleId)
                        true
                    } else {
                        false
                    }
                } else {
                    deleteMyTimeCapsule(timeCapsuleId)
                    true
                }
            }
        }
    }
}