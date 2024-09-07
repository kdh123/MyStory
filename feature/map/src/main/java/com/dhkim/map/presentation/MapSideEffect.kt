package com.dhkim.map.presentation

sealed interface MapSideEffect {

    data class BottomSheet(val isHide: Boolean = true) : MapSideEffect
}