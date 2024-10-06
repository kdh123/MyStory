package com.dhkim.home.presentation.add

import com.dhkim.location.domain.Place

sealed interface AddTimeCapsuleAction {

    data object SaveTimeCapsule : AddTimeCapsuleAction
    data class SetCheckShare(val isCheck: Boolean) : AddTimeCapsuleAction
    data class SetCheckLocation(val isCheck: Boolean) : AddTimeCapsuleAction
    data class SetSelectImageIndex(val index: Int) : AddTimeCapsuleAction
    data class SetOpenDate(val date: String) : AddTimeCapsuleAction
    data class Typing(val text: String) : AddTimeCapsuleAction
    data class CheckSharedFriend(val friendId: String) : AddTimeCapsuleAction
    data class Query(val query: String) : AddTimeCapsuleAction
    data class PlaceClick(val place: Place) : AddTimeCapsuleAction
    data class SearchAddress(val lat: String, val lng: String) : AddTimeCapsuleAction
    data class InitPlace(val place: Place) : AddTimeCapsuleAction
    data class AddFriend(val friendId: String) : AddTimeCapsuleAction
    data class AddImage(val imageUrl: String) : AddTimeCapsuleAction
}