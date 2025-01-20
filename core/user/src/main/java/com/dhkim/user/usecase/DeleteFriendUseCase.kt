package com.dhkim.user.usecase

import com.dhkim.user.datasource.isSuccessful
import com.dhkim.user.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class DeleteFriendUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(userId: String): Flow<isSuccessful> {
        return userRepository.deleteFriend(userId).flatMapConcat { isSuccessful ->
            if (isSuccessful) {
                userRepository.deleteLocalFriend(userId)
                flowOf(true)
            } else {
                flowOf(false)
            }
        }
    }
}