package com.dhkim.user.domain.usecase

import com.dhkim.user.data.datasource.isSuccessful
import com.dhkim.user.domain.model.Friend
import com.dhkim.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateFriendInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(friend: Friend): Flow<isSuccessful> {
        return userRepository.updateFriend(friend)
    }
}