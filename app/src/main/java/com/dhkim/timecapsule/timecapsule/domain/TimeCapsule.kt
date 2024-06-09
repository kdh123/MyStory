package com.dhkim.timecapsule.timecapsule.domain

data class TimeCapsule(
    val id: String,
    val date: String,
    val openDate: String,
    val lat: String,
    val lng: String,
    val address: String,
    val content: String,
    val medias: List<String>,
    val checkLocation: Boolean,
    val isOpened: Boolean,
    val sharedFriends: List<String>,
    val isReceived: Boolean,
    val sender: String
) {
    fun toMyTimeCapsule(): MyTimeCapsule {
        return MyTimeCapsule(
            id, date, openDate, lat, lng, address, content, medias, checkLocation, isOpened, sharedFriends
        )
    }

    fun toReceivedCapsule(): ReceivedTimeCapsule {
        return ReceivedTimeCapsule(
            id = id,
            date = date,
            openDate = openDate,
            sender = sender,
            lat = lat,
            lng = lng,
            address = address,
            content = content,
            checkLocation = checkLocation,
            isOpened = isOpened
        )
    }
}
