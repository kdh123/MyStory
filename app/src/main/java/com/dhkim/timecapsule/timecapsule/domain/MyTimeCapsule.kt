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
): BaseTimeCapsule
