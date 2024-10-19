package com.dhkim.home.domain

import com.dhkim.user.model.UserId
import com.dhkim.user.model.Nickname

data class MyTimeCapsule(
    val id: String = "",
    val date: String = "",
    val openDate: String = "",
    val lat: String = "0.0",
    val lng: String = "0.0",
    val placeName: String = "",
    val address: String = "",
    val content: String = "",
    val images: List<String> = listOf(),
    val videos: List<String> = listOf(),
    val checkLocation: Boolean = false,
    val isOpened: Boolean = false,
    val sharedFriends: List<UserId> = listOf()
) {
    fun toTimeCapsule(myId: String, profileImage: String, sharedFriends: List<Nickname>): TimeCapsule {
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
            images = images,
            videos = videos,
            checkLocation = checkLocation,
            isOpened = isOpened,
            sharedFriends = sharedFriends,
            isReceived = false,
            sender = myId
        )
    }
}
