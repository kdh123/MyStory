package com.dhkim.timecapsule.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.profile.data.di.FirebaseModule
import com.dhkim.timecapsule.profile.domain.Friend
import com.dhkim.timecapsule.profile.domain.User
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias UserId = String

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @FirebaseModule.FirebaseDatabase private val database: DatabaseReference,
    @FirebaseModule.UserFirebaseDatabase private val userDatabase: DatabaseReference
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val myId = userRepository.getMyId()
            _uiState.value = _uiState.value.copy(user = User().copy(id = myId))
        }

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.value as? Map<*, *>
                val friends = (data?.get("dh") as? Map<*, *>)?.get("friends") as? List<Map<*, *>> ?: listOf()
                val requests = (data?.get("dh") as? Map<*, *>)?.get("requests") as? List<String> ?: listOf()
                val getFriends = friends.map {
                    Friend(it["id"] as String, it["pending"] as Boolean)
                }
                val currentUser = _uiState.value.user

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = currentUser.copy(
                        friends = getFriends,
                        requests = requests
                    )
                )
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }

        userDatabase.addValueEventListener(userListener)
    }

    fun onQuery(query: String) {
        val searchResult = _uiState.value.searchResult
        _uiState.value = _uiState.value.copy(searchResult = searchResult.copy(query = query))
    }

    fun searchUser() {
        viewModelScope.launch {
            val myId = uiState.value.user.id
            val searchResult = uiState.value.searchResult

            userRepository.searchUser(searchResult.query).first()?.let { isExist ->
                if (isExist) {
                    val isMe = searchResult.query == myId
                    _uiState.value = _uiState.value.copy(searchResult = searchResult.copy(userId = searchResult.query, isMe = isMe))
                } else {
                    _uiState.value = _uiState.value.copy(searchResult = searchResult.copy(userId = ""))
                }
            }
        }
    }

    fun addFriend() {
        val searchUserId = _uiState.value.searchResult.userId
        val friendsIds = _uiState.value.user.friends.map { it.id }
        val requests = _uiState.value.user.requests
        if (friendsIds.contains(searchUserId) || requests.contains(searchUserId)) {

        } else {
            val friends = _uiState.value.user.friends.toMutableList().apply {
                add(Friend(id = searchUserId, isPending = true))
            }
            val user = _uiState.value.user.copy(friends = friends)
            userRepository.updateUser(user = user)
        }
    }
}