package com.dhkim.user.domain.usecase

import com.dhkim.user.data.datasource.isSuccessful
import com.dhkim.user.domain.model.User
import com.dhkim.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateFriendCodeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(fcmToken: String, profileImage: Int): Flow<isSuccessful> {
        return flow {
            val userId = StringBuilder().apply {
                repeat(6) {
                    when ((0..2).random()) {
                        0 -> append(('0'.code..'9'.code).random().toChar())
                        1 -> append(('A'.code..'Z'.code).random().toChar())
                        2 -> append(('a'.code..'z'.code).random().toChar())
                    }
                }
            }.toString()

            val uuid = (0..10_000_000_000).random().toString()
            val isRegisterSuccessful = userRepository.registerPush(uuid, fcmToken)

            if (isRegisterSuccessful) {
                val user = User(id = userId, profileImage = "$profileImage", uuid = uuid)
                val isSuccessful: Boolean = userRepository.updateUser(user).first()

                if (isSuccessful) {
                    userRepository.run {
                        updateUserId(userId = userId)
                        updateProfileImage(profileImage = "$profileImage")
                        updateUuid(uuid = uuid)
                        updateFcmToken(fcmToken = fcmToken)
                    }
                    emit(true)
                }
            } else {
                emit(false)
            }
        }
    }
}