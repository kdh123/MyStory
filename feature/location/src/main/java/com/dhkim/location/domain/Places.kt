package com.dhkim.location.domain

data class Places(
    val isEnd: Boolean = true,
    val places: List<Place> = listOf()
)
