package com.dhkim.timecapsule.search.domain

data class Place(
    val id: String,
    val name: String,
    val lat: String,
    val lng: String,
    val address: String,
    val category: String,
    val distance: String,
    val phone: String,
    val url: String
)
