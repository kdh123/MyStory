package com.dhkim.home.domain

import com.dhkim.common.Constants

data class Host(
    val id: String = "",
    val nickname: String = id,
    val profileImage: String = "0"
)

data class TimeCapsule(
    val id: String = "",
    val host: Host = Host(),
    val date: String = "",
    val openDate: String = "",
    val lat: String = "${Constants.defaultLocation.latitude}",
    val lng: String = "${Constants.defaultLocation.longitude}",
    val placeName: String = "",
    val address: String = "",
    val content: String = "",
    val medias: List<String> = listOf(),
    val checkLocation: Boolean = false,
    val isOpened: Boolean = false,
    val sharedFriends: List<String> = listOf(),
    val isReceived: Boolean = false,
    val sender: String = ""
)
