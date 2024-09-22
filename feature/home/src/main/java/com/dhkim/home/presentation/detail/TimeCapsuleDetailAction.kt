package com.dhkim.home.presentation.detail

sealed interface TimeCapsuleDetailAction {

    data class Init(val timeCapsuleId: String, val isReceived: Boolean) : TimeCapsuleDetailAction
    data class DeleteTimeCapsule(val timeCapsuleId: String) : TimeCapsuleDetailAction
}