package com.dhkim.home.domain.usecase

import com.dhkim.home.data.dataSource.remote.Uuid
import com.dhkim.home.data.dataSource.remote.isSuccessful
import com.dhkim.home.domain.repository.TimeCapsuleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShareTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {

    operator fun invoke(myId: String,
                        myProfileImage: String,
                        timeCapsuleId: String,
                        sharedFriends: List<Uuid>,
                        openDate: String,
                        content: String,
                        lat: String,
                        lng: String,
                        placeName: String,
                        address: String,
                        checkLocation: Boolean
    ): Flow<isSuccessful> {
        return timeCapsuleRepository.shareTimeCapsule(
            myId = myId,
            myProfileImage = myProfileImage,
            timeCapsuleId = timeCapsuleId,
            sharedFriends = sharedFriends,
            openDate = openDate,
            content = content,
            lat = lat,
            lng = lng,
            placeName = placeName,
            address = address,
            checkLocation = checkLocation
        )
    }
}