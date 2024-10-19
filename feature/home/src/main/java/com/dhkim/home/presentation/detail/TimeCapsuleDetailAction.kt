package com.dhkim.home.presentation.detail

sealed interface TimeCapsuleDetailAction {

    data object DeleteTimeCapsule : TimeCapsuleDetailAction
}