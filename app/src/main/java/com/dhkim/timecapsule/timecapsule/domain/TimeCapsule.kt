package com.dhkim.timecapsule.timecapsule.domain

import com.dhkim.timecapsule.common.Constants

data class Host(
    val id: String = "",
    val profileImage: String = ""
)

data class TimeCapsule(
    val id: String = "",
    val host: Host = Host(),
    val date: String = "",
    val openDate: String = "",
    val lat: String = "${Constants.defaultLocation.latitude}",
    val lng: String = "${Constants.defaultLocation.longitude}",
    val address: String = "",
    val content: String = "",
    val medias: List<String> = listOf(),
    val checkLocation: Boolean = false,
    val isOpened: Boolean = false,
    val sharedFriends: List<String> = listOf(),
    val isReceived: Boolean = false,
    val sender: String = ""
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
