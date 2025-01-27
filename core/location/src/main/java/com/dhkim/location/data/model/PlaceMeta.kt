package com.dhkim.location.data.model

internal data class PlaceMeta(
    val is_end: Boolean,
    val pageable_count: Int,
    val same_name: SameName,
    val total_count: Int
)