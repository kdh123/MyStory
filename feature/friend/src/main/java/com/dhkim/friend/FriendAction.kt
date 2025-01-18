package com.dhkim.friend

import com.dhkim.user.model.Friend

sealed interface FriendAction {

    data class Query(val query: String) : FriendAction
    data object CreateFriendCode : FriendAction
    data object SearchUser : FriendAction
    data object AddFriend : FriendAction
    data class AcceptFriend(val friend: Friend) : FriendAction
    data class DeleteFriend(val userId: String) : FriendAction
}