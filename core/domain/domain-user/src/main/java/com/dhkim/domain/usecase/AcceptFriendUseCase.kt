package com.dhkim.domain.usecase

import com.dhkim.domain.repository.UserRepository
import com.dhkim.domain.repository.isSuccessful
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AcceptFriendUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(userId: String, userProfileImage: String, userUuid: String): Flow<isSuccessful> {
        return userRepository.acceptFriend(userId, userProfileImage, userUuid)
    }
}