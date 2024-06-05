package com.dhkim.timecapsule.search.data.model

data class PlaceMeta(
    val is_end: Boolean,
    val pageable_count: Int,
    val same_name: SameName,
    val total_count: Int
)