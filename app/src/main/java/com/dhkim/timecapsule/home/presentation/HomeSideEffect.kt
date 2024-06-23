package com.dhkim.timecapsule.home.presentation

sealed interface HomeSideEffect {

    data object None: HomeSideEffect
    data class BottomSheet(val isHide: Boolean = true) : HomeSideEffect
}