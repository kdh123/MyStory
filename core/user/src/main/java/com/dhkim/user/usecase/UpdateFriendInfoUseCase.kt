package com.dhkim.user.usecase

import com.dhkim.user.datasource.isSuccessful
import com.dhkim.user.model.Friend
import com.dhkim.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateFriendInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(friend: Friend): Flow<isSuccessful> {
        return userRepository.updateFriend(friend)
    }
}