package com.dhkim.user.domain.usecase

import com.dhkim.common.CommonResult
import com.dhkim.user.domain.model.User
import com.dhkim.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchFriendUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(userId: String): Flow<CommonResult<User?>> {
        return userRepository.searchUser(userId)
    }
}