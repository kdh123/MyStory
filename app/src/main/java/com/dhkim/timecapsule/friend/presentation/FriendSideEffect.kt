package com.dhkim.timecapsule.friend.presentation

sealed interface FriendSideEffect {

    data object None : FriendSideEffect
    data class ShowBottomSheet(val show: Boolean) : FriendSideEffect
    data class ShowDialog(val show: Boolean) : FriendSideEffect
    data class Message(val message: String) : FriendSideEffect
    data class ShowKeyboard(val show: Boolean) : FriendSideEffect
}