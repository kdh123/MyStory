package com.dhkim.story.domain.usecase

import com.dhkim.common.DateUtil
import com.dhkim.story.domain.model.MyTimeCapsule
import com.dhkim.story.domain.model.SharedFriend
import com.dhkim.story.domain.repository.TimeCapsuleRepository
import com.dhkim.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveMyTimeCapsuleUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val shareTimeCapsuleUseCase: ShareTimeCapsuleUseCase
) {

    operator fun invoke(
        imageUrls: List<String>,
        sharedFriends: List<SharedFriend>,
        openDate: String,
        content: String,
        lat: String,
        lng: String,
        placeName: String,
        address: String,
        checkLocation: Boolean,
        isShare: Boolean
    ): Flow<isSuccessful> {
        return flow {
            val timeCapsuleId = "${System.currentTimeMillis()}"
            val timeCapsule = MyTimeCapsule(
                id = timeCapsuleId,
                date = DateUtil.todayDate(),
                openDate = openDate,
                lat = lat,
                lng = lng,
                placeName = placeName,
                address = address,
                images = imageUrls,
                content = content,
                checkLocation = checkLocation,
                isOpened = false,
                sharedFriends = sharedFriends.map { it.userId }
            )

            if (isShare) {
                val isSuccessful = shareTimeCapsuleUseCase(
                    myId = userRepository.getMyId(),
                    myProfileImage = "${userRepository.getProfileImage()}",
                    timeCapsuleId = timeCapsuleId,
                    sharedFriends = sharedFriends.map { it.uuid },
                    openDate = openDate,
                    content = content,
                    lat = lat,
                    lng = lng,
                    placeName = placeName,
                    address = address,
                    checkLocation = checkLocation
                ).first()

                if (isSuccessful) timeCapsuleRepository.saveMyTimeCapsule(timeCapsule = timeCapsule)
                emit(isSuccessful)
            } else {
                timeCapsuleRepository.saveMyTimeCapsule(timeCapsule = timeCapsule)
                emit(true)
            }
        }
    }
}