package com.dhkim.friend.changeInfo

sealed interface ChangeFriendInfoSideEffect {

    data object None: ChangeFriendInfoSideEffect
    data class Completed(val isCompleted: Boolean): ChangeFriendInfoSideEffect
    data class Message(val message: String): ChangeFriendInfoSideEffect
}