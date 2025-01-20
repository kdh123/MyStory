package com.dhkim.location.domain.model

data class Places(
    val isEnd: Boolean = true,
    val places: List<Place> = listOf()
)
