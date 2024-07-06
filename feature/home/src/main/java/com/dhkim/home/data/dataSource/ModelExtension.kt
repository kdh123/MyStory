package com.dhkim.home.data.dataSource

import com.dhkim.database.entity.MyTimeCapsuleEntity
import com.dhkim.database.entity.ReceivedTimeCapsuleEntity
import com.dhkim.database.entity.SendTimeCapsuleEntity
import com.dhkim.home.domain.MyTimeCapsule
import com.dhkim.home.domain.ReceivedTimeCapsule
import com.dhkim.home.domain.SendTimeCapsule

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
        images = images,
        videos = videos,
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