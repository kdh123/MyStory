package com.dhkim.friend.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.CommonResult
import com.dhkim.user.domain.Friend
import com.dhkim.user.domain.LocalFriend
import com.dhkim.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<FriendSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            val myId = userRepository.getMyId()
            val myProfileImage = userRepository.getProfileImage().toString()
            val user = _uiState.value.user

            _uiState.value = _uiState.value.copy(user = user.copy(id = myId, profileImage = myProfileImage))
        }

        viewModelScope.launch(Dispatchers.IO) {
            combine(userRepository.getMyInfo(), userRepository.getAllFriend()) { user, friends ->
                val remoteFriends = user.friends
                val pendingFriends = user.friends.filter { it.isPending }

                friends.map { it.id }.filter { id -> !remoteFriends.map { it.id }.contains(id) }.forEach {  id ->
                    userRepository.deleteLocalFriend(id)
                }

                val localFriends = userRepository.getAllFriend().first()

                remoteFriends
                    .filter { !it.isPending }
                    .map { it.id }
                    .filter { id -> !localFriends.map { it.id }.contains(id) }
                    .forEach { id ->
                        val friend = user.friends.first { it.id == id }
                        val localFriend = LocalFriend(
                            id = friend.id,
                            nickname = friend.id,
                            profileImage = friend.profileImage,
                            uuid = friend.uuid
                        )
                        userRepository.saveFriend(localFriend)
                    }

                user.copy(friends = localFriends.map { it.toFriend() } + pendingFriends)
            }.catch { }
                .collect {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = it
                    )
                }
        }
    }

    fun onQuery(query: String) {
        val searchResult = _uiState.value.searchResult
        _uiState.value = _uiState.value.copy(searchResult = searchResult.copy(query = query))
    }

    fun addFriend() {
        viewModelScope.launch {
            val searchUserId = _uiState.value.searchResult.userId ?: ""
            val searchUserProfileImage = _uiState.value.searchResult.userProfileImage

            userRepository.addFriend(searchUserId, searchUserProfileImage)
                .catch {
                    _sideEffect.emit(FriendSideEffect.Message(message = "친구 추가에 실패하였습니다."))
                }
                .collect { isSuccessful ->
                    if (isSuccessful) {
                        _sideEffect.emit(FriendSideEffect.ShowKeyboard(show = false))
                    } else {
                        _sideEffect.emit(FriendSideEffect.Message(message = "친구 추가에 실패하였습니다."))
                    }
                }
        }
    }

    fun deleteFriend(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteFriend(userId = userId)
                .catch {
                    _sideEffect.emit(FriendSideEffect.ShowDialog(show = false))
                    _sideEffect.emit(FriendSideEffect.Message(message = "친구 삭제에 실패하였습니다."))
                }.collect { isSuccessful ->
                    if (isSuccessful) {
                        userRepository.deleteLocalFriend(userId)
                        _sideEffect.emit(FriendSideEffect.ShowKeyboard(show = false))
                        _sideEffect.emit(FriendSideEffect.ShowDialog(show = false))
                    } else {
                        _sideEffect.emit(FriendSideEffect.ShowDialog(show = false))
                        _sideEffect.emit(FriendSideEffect.Message(message = "친구 삭제에 실패하였습니다."))
                    }
                }
        }
    }

    fun acceptFriend(friend: Friend) {
        viewModelScope.launch {
            userRepository.acceptFriend(friend.id, friend.profileImage, friend.uuid)
                .catch {
                    _sideEffect.emit(FriendSideEffect.Message(message = "친구 추가에 실패하였습니다."))
                }
                .collect { isSuccessful ->
                    if (!isSuccessful) {
                        _sideEffect.emit(FriendSideEffect.Message(message = "친구 추가에 실패하였습니다."))
                    }
                }
        }
    }

    fun searchUser() {
        viewModelScope.launch {
            val myId = uiState.value.user.id
            val searchResult = uiState.value.searchResult

            userRepository.searchUser(searchResult.query)
                .catch {
                    _sideEffect.emit(FriendSideEffect.Message(message = "친구 찾기에 실패하였습니다."))
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .collect { result ->
                    when (result) {
                        is CommonResult.Success -> {
                            val user = result.data

                            if (user != null) {
                                val isMe = searchResult.query == myId
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    searchResult = searchResult.copy(userId = user.id, userProfileImage = user.profileImage, isMe = isMe)
                                )
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    searchResult = searchResult.copy(userId = null)
                                )
                            }
                        }

                        is CommonResult.Error -> {
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            _sideEffect.emit(FriendSideEffect.Message(message = "친구 찾기에 실패하였습니다."))
                        }
                    }
                }
        }
    }
}