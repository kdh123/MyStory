package com.dhkim.friend.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.CommonResult
import com.dhkim.friend.R
import com.dhkim.user.domain.Friend
import com.dhkim.user.domain.UserRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<FriendSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    private val words = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "k", "L", "M", "N", "O", "P",
        "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i",
        "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1",
        "2", "3", "4", "5", "6", "7", "8", "9"
    )

    private val profileImages = listOf(
        R.drawable.ic_smile_blue,
        R.drawable.ic_smile_violet,
        R.drawable.ic_smile_green,
        R.drawable.ic_smile_orange
    )

    init {
        getMyInfo()
    }

    fun onAction(action: FriendAction) {
        when (action) {
            is FriendAction.AcceptFriend -> {
                acceptFriend(friend = action.friend)
            }

            FriendAction.AddFriend -> {
                addFriend()
            }

            is FriendAction.DeleteFriend -> {
                deleteFriend(userId = action.userId)
            }

            is FriendAction.Query -> {
                onQuery(query = action.query)
            }

            FriendAction.SearchUser -> {
                searchUser()
            }

            FriendAction.CreateCode -> {
                createCode()
            }
        }
    }

    private fun getMyInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val myId = userRepository.getMyId()

            if (myId.isEmpty()) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                return@launch
            }

            userRepository.getMyInfo().catch {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCreatingCode = false
                )
            }.collect {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCreatingCode = false,
                    myInfo = it
                )
            }
        }
    }

    private fun createCode() {
        _uiState.value = _uiState.value.copy(isCreatingCode = true)

        viewModelScope.launch {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("fcm", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                viewModelScope.launch {
                    val fcmToken = task.result
                    val profileImage = profileImages[(0..3).random()]

                    val userId = StringBuilder().apply {
                        append(words[words.indices.random()])
                        append(words[words.indices.random()])
                        append(words[words.indices.random()])
                        append(words[words.indices.random()])
                        append(words[words.indices.random()])
                        append(words[words.indices.random()])
                    }

                    val isSuccessful = userRepository.signUp(
                        userId = "$userId",
                        profileImage = "$profileImage",
                        fcmToken = fcmToken
                    )

                    if (isSuccessful) {
                        getMyInfo()
                        //_sideEffect.send(FriendSideEffect.Message(message = "코드 생성 성공!!"))
                    } else {
                        _uiState.value = _uiState.value.copy(isCreatingCode = false)
                        _sideEffect.send(FriendSideEffect.Message(message = "코드 생성에 실패하였습니다. 다시 시도해주세요."))
                    }
                }
            }).addOnFailureListener {
                viewModelScope.launch {
                    _sideEffect.send(FriendSideEffect.Message(message = "코드 생성에 실패하였습니다. 다시 시도해주세요."))
                }
            }
        }
    }

    private fun onQuery(query: String) {
        val searchResult = _uiState.value.searchResult
        _uiState.value = _uiState.value.copy(searchResult = searchResult.copy(query = query))
    }

    private fun addFriend() {
        viewModelScope.launch {
            val searchUserId = _uiState.value.searchResult.userId ?: ""
            val searchUserProfileImage = _uiState.value.searchResult.userProfileImage

            userRepository.addFriend(searchUserId, searchUserProfileImage)
                .catch {
                    _sideEffect.send(FriendSideEffect.Message(message = "친구 추가에 실패하였습니다."))
                }
                .collect { isSuccessful ->
                    if (isSuccessful) {
                        _sideEffect.send(FriendSideEffect.ShowKeyboard(show = false))
                    } else {
                        _sideEffect.send(FriendSideEffect.Message(message = "친구 추가에 실패하였습니다."))
                    }
                }
        }
    }

    private fun deleteFriend(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteFriend(userId = userId)
                .catch {
                    _sideEffect.send(FriendSideEffect.ShowDialog(show = false))
                    _sideEffect.send(FriendSideEffect.Message(message = "친구 삭제에 실패하였습니다."))
                }.collect { isSuccessful ->
                    if (isSuccessful) {
                        userRepository.deleteLocalFriend(userId)
                        _sideEffect.send(FriendSideEffect.ShowKeyboard(show = false))
                        _sideEffect.send(FriendSideEffect.ShowDialog(show = false))
                    } else {
                        _sideEffect.send(FriendSideEffect.ShowDialog(show = false))
                        _sideEffect.send(FriendSideEffect.Message(message = "친구 삭제에 실패하였습니다."))
                    }
                }
        }
    }

    private fun acceptFriend(friend: Friend) {
        viewModelScope.launch {
            userRepository.acceptFriend(friend.id, friend.profileImage, friend.uuid)
                .catch {
                    _sideEffect.send(FriendSideEffect.Message(message = "친구 추가에 실패하였습니다."))
                }
                .collect { isSuccessful ->
                    if (!isSuccessful) {
                        _sideEffect.send(FriendSideEffect.Message(message = "친구 추가에 실패하였습니다."))
                    }
                }
        }
    }

    private fun searchUser() {
        viewModelScope.launch {
            val myId = uiState.value.myInfo.id
            val searchResult = uiState.value.searchResult

            userRepository.searchUser(searchResult.query)
                .catch {
                    _sideEffect.send(FriendSideEffect.Message(message = "친구 찾기에 실패하였습니다."))
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
                                    searchResult = searchResult.copy(
                                        userId = user.id,
                                        userProfileImage = user.profileImage,
                                        isMe = isMe
                                    )
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
                            _sideEffect.send(FriendSideEffect.Message(message = "친구 찾기에 실패하였습니다."))
                        }
                    }
                }
        }
    }
}