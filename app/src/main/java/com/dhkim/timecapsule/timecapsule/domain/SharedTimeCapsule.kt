package com.dhkim.timecapsule.timecapsule.domain

data class SharedTimeCapsule(
    val sender: String,
    val profileImage: String,
    val openDate: String,
    val content: String,
    val lat: String,
    val lng: String,
    val address: String,
    val checkLocation: Boolean
)