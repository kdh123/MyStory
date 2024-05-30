package com.dhkim.timecapsule.search.domain

data class Places(
    val isEnd: Boolean = true,
    val places: List<Place> = listOf()
)
