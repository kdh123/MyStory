package com.dhkim.location.domain.model

import java.io.Serializable

data class Place(
    val id: String = "",
    val name: String = "",
    val lat: String = "",
    val lng: String = "",
    val address: String = "",
    val category: String = "",
    val distance: String = "",
    val phone: String = "",
    val url: String = ""
): Serializable
