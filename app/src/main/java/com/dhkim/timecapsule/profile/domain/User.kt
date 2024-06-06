package com.dhkim.timecapsule.profile.domain

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

typealias UserId = String

@IgnoreExtraProperties
data class User(
    val id: String,
    val profileImageUrl: String,
    val friends: List<Friend>,
    val requests: List<UserId>
) {
    @Exclude
    fun toFriends(): Map<String, Any?> {
        return mapOf(
            "friends" to friends
        )
    }

    @Exclude
    fun toRequests(): Map<String, Any?> {
        return mapOf(
            "requests" to requests
        )
    }
}

data class Friend(
    val id: String = "",
    val isPending: Boolean = true
)
