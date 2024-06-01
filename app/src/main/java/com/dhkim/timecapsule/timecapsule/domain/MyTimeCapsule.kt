package com.dhkim.timecapsule.timecapsule.domain


data class MyTimeCapsule(
    val id: String,
    val date: String,
    val openDate: String,
    val lat: String,
    val lng: String,
    val address: List<String>,
    val content: String,
    val medias: List<String>,
    val checkLocation: Boolean,
    val isOpened: Boolean
)
