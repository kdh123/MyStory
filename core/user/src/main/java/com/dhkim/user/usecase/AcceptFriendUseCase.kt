package com.dhkim.user.usecase

import com.dhkim.user.datasource.isSuccessful
import com.dhkim.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AcceptFriendUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(userId: String, userProfileImage: String, userUuid: String): Flow<isSuccessful> {
        return userRepository.acceptFriend(userId, userProfileImage, userUuid)
    }
}