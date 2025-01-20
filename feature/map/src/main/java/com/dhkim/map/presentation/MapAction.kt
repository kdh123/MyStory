package com.dhkim.map.presentation

import com.dhkim.location.domain.model.Category
import com.dhkim.location.domain.model.Place

sealed interface MapAction {

    data class SelectPlace(val place: Place) : MapAction
    data class SearchPlacesByCategory(val category: Category, val lat: String, val lng: String) : MapAction
    data class SearchPlacesByKeyword(val query: String, val lat: String, val lng: String) : MapAction
    data class CloseSearch(val isPlaceSelected: Boolean) : MapAction
}