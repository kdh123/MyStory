package com.dhkim.timecapsule.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.common.data.di.FirebaseModule
import com.dhkim.timecapsule.profile.domain.User
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

        viewModelScope.launch {
            userRepository.getMyInfo()
                .catch {  }
                .collect {
                    if (it.id.isNotEmpty()) {
                        _uiState.value = _uiState.value.copy(isLoading = false, user = it)
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                }
        }
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
        viewModelScope.launch {
            val searchUserId = _uiState.value.searchResult.userId
            val friendsIds = _uiState.value.user.friends.map { it.id }
            val requestIds = _uiState.value.user.requests.map { it.id }

            if (friendsIds.contains(searchUserId) || requestIds.contains(searchUserId)) {

            } else {
                userRepository.addFriends(searchUserId).catch { }
                    .collect { isSuccessful ->
                        if (!isSuccessful) {

                        }
                    }
            }
        }
    }
}