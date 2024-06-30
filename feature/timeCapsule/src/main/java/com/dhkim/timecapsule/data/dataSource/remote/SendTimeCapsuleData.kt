package com.dhkim.timecapsule.data.dataSource.remote

data class SendTimeCapsuleData(
    val id: String = "${System.currentTimeMillis()}",
    val address: String = "",
    val content: String = "",
    val lat: String = "",
    val lng: String = "",
    val openDate: String = "",
    val title: String = "",
    val userId: String = ""
)