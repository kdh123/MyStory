package com.dhkim.story.domain.model

data class SharedTimeCapsule(
    val timeCapsuleId: String,
    val sender: String,
    val profileImage: String,
    val openDate: String,
    val content: String,
    val lat: String,
    val lng: String,
    val placeName: String,
    val address: String,
    val checkLocation: Boolean
)