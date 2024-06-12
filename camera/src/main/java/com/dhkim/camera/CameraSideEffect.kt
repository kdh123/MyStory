package com.dhkim.camera

sealed interface CameraSideEffect {

    data object None: CameraSideEffect

    data class Completed(val isCompleted: Boolean): CameraSideEffect
}