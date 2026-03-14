package com.dhkim.domain.usecase

import com.dhkim.domain.model.Friend
import com.dhkim.domain.repository.UserRepository
import com.dhkim.domain.repository.isSuccessful
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateFriendInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(friend: Friend): Flow<isSuccessful> {
        return userRepository.updateFriend(friend)
    }
}