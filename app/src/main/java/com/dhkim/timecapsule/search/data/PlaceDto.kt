package com.dhkim.timecapsule.search.data

import com.dhkim.timecapsule.search.domain.Places

data class PlaceDto(
    val documents: List<PlaceDocument>,
    val meta: PlaceMeta
) {
    fun toPlaces(): Places {
        return Places(
            isEnd = meta.is_end,
            places = documents.map { it.toPlace() }
        )
    }
}