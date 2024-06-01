package com.dhkim.timecapsule.timecapsule.domain


data class SendTimeCapsule(
    val id: String,
    val date: String,
    val openDate: String,
    val receiver: String,
    val lat: String,
    val lng: String,
    val address: List<String>,
    val content: String,
    val checkLocation: Boolean
)
