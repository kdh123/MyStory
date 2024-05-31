package com.dhkim.timecapsule.home

import com.dhkim.timecapsule.home.domain.Category

sealed interface HomeAction {
    data class OnSearchPlacesByCategory(
        val category: Category,
        val lat: String,
        val lng: String
    ) : HomeAction
}