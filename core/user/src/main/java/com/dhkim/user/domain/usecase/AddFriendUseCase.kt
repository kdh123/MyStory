package com.dhkim.user.domain.usecase

import com.dhkim.user.data.datasource.isSuccessful
import com.dhkim.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddFriendUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(userId: String, userProfileImage: String): Flow<isSuccessful> {
        return flow {
            val isSuccessful = userRepository.addFriend(userId, userProfileImage).first()
            emit(isSuccessful)
        }
    }
}