package com.dhkim.timecapsule.timecapsule.domain

import com.dhkim.timecapsule.profile.domain.UserId

data class MyTimeCapsule(
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
    val sharedFriends: List<UserId>
) : BaseTimeCapsule {

    fun toTimeCapsule(): TimeCapsule {
        return TimeCapsule(
            id = id,
            date = date,
            openDate = openDate,
            lat = lat,
            lng = lng,
            address = address,
            content = content,
            medias = medias,
            checkLocation = checkLocation,
            isOpened = isOpened,
            sharedFriends = sharedFriends,
            isReceived = false,
            sender = id
        )
    }
}
