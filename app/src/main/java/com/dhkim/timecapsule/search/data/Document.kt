package com.dhkim.timecapsule.search.data

import com.dhkim.timecapsule.search.domain.Place

data class Document(
    val address_name: String,
    val category_group_code: String,
    val category_group_name: String,
    val category_name: String,
    val distance: String,
    val id: String,
    val phone: String,
    val place_name: String,
    val place_url: String,
    val road_address_name: String,
    val x: String,
    val y: String
) {
    fun toPlace(): Place {
        return Place(
            id = id,
            name = place_name,
            lat = y,
            lng = x,
            category = category_name,
            distance = distance,
            phone = phone,
            url = place_url,
            address = road_address_name,
        )
    }
}