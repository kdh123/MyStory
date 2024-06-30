package com.dhkim.timecapsule.domain


data class SendTimeCapsule(
    val id: String,
    val date: String,
    val openDate: String,
    val sharedFriends: String,
    val lat: String,
    val lng: String,
    val address: String,
    val content: String,
    val checkLocation: Boolean,
    val isChecked: Boolean
): BaseTimeCapsule
