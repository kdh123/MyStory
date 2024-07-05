package com.dhkim.home.domain

import com.dhkim.user.domain.UserId

data class MyTimeCapsule(
    val id: String,
    val date: String,
    val openDate: String,
    val lat: String,
    val lng: String,
    val placeName: String,
    val address: String,
    val content: String,
    val medias: List<String>,
    val checkLocation: Boolean,
    val isOpened: Boolean,
    val sharedFriends: List<UserId>
) : BaseTimeCapsule {

    fun toTimeCapsule(myId: String, profileImage: String): TimeCapsule {
        return TimeCapsule(
            id = id,
            date = date,
            host = Host(
                id = id,
                profileImage = profileImage
            ),
            openDate = openDate,
            lat = lat,
            lng = lng,
            placeName = placeName,
            address = address,
            content = content,
            medias = medias,
            checkLocation = checkLocation,
            isOpened = isOpened,
            sharedFriends = sharedFriends,
            isReceived = false,
            sender = myId
        )
    }
}
