package com.dhkim.timecapsule.map.presentation

sealed interface MapSideEffect {

    data object None: MapSideEffect
    data class BottomSheet(val isHide: Boolean = true) : MapSideEffect
}