package com.dhkim.user.domain.usecase

import com.dhkim.user.domain.model.User
import com.dhkim.user.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetMyInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<User> {
        return flow { emit(userRepository.getMyId()) }.flatMapLatest {
            if (it.isNotEmpty()) {
                userRepository.getMyInfo(myId = it)
            } else {
                flowOf(User())
            }
        }
    }
}