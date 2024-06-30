package com.dhkim.timecapsule.timecapsule.data.dataSource

import com.dhkim.database.entity.MyTimeCapsuleEntity
import com.dhkim.database.entity.ReceivedTimeCapsuleEntity
import com.dhkim.database.entity.SendTimeCapsuleEntity
import com.dhkim.timecapsule.timecapsule.domain.MyTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.ReceivedTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.SendTimeCapsule

fun MyTimeCapsuleEntity.toMyTimeCapsule(): MyTimeCapsule {
    return MyTimeCapsule(
        id = id,
        date = date,
        openDate = openDate,
        lat = lat,
        lng = lng,
        placeName = placeName,
        address = address,
        content = content,
        medias = medias,
        checkLocation = checkLocation,
        isOpened = isOpened,
        sharedFriends = sharedFriends
    )
}

fun SendTimeCapsuleEntity.toSenderTimeCapsule(): SendTimeCapsule {
    return SendTimeCapsule(
        id = id,
        date = date,
        openDate = openDate,
        sharedFriends = receiver,
        lat = lat,
        lng = lng,
        address = address,
        content = content,
        checkLocation = checkLocation,
        isChecked = isChecked
    )
}

fun ReceivedTimeCapsuleEntity.toReceivedTimeCapsule(): ReceivedTimeCapsule {
    return ReceivedTimeCapsule(
        id = id,
        date = date,
        openDate = openDate,
        sender = sender,
        profileImage = profileImage,
        lat = lat,
        lng = lng,
        placeName = placeName,
        address = address,
        content = content,
        checkLocation = checkLocation,
        isOpened = isOpened
    )
}