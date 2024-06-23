package com.dhkim.timecapsule.location.data.model

import com.dhkim.timecapsule.location.domain.Places

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