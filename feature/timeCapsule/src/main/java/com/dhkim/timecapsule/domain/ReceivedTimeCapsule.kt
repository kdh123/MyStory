package com.dhkim.timecapsule.domain

data class ReceivedTimeCapsule(
    val id: String = "${System.currentTimeMillis()}",
    val date: String = "",
    val openDate: String = "",
    val sender: String = "",
    val profileImage: String = "0",
    val lat: String = "",
    val lng: String = "",
    val placeName: String = "",
    val address: String = "",
    val content: String = "",
    val checkLocation: Boolean = false,
    val isOpened: Boolean = false
) : BaseTimeCapsule {

    fun toTimeCapsule(): TimeCapsule {
        return TimeCapsule(
            id = id,
            date = date,
            host = Host(
                id = sender,
                profileImage = profileImage
            ),
            openDate = openDate,
            lat = lat,
            lng = lng,
            placeName = placeName,
            address = address,
            content = content,
            medias = listOf(),
            checkLocation = checkLocation,
            isOpened = isOpened,
            sharedFriends = listOf(),
            isReceived = true,
            sender = sender
        )
    }
}
