package com.dhkim.timecapsule.timecapsule.domain

data class ReceivedTimeCapsule(
    val id: String,
    val date: String,
    val openDate: String,
    val sender: String,
    val lat: String,
    val lng: String,
    val address: String,
    val content: String,
    val checkLocation: Boolean,
    val isOpened: Boolean
): BaseTimeCapsule {

    fun toTimeCapsule(): TimeCapsule {
        return TimeCapsule(
            id = id,
            date = date,
            openDate = openDate,
            lat = lat,
            lng = lng,
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
