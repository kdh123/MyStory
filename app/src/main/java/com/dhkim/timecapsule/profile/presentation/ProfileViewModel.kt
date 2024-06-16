package com.dhkim.timecapsule.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.common.presentation.profileImage
import com.dhkim.timecapsule.profile.domain.Friend
import com.dhkim.timecapsule.profile.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ProfileSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            val myId = userRepository.getMyId()
            val myProfileImage = userRepository.getProfileImage().profileImage()
            val user = _uiState.value.user
            _uiState.value = _uiState.value.copy(user = user.copy(id = myId, profileImage = myProfileImage))
        }

        viewModelScope.launch {
            userRepository.getMyInfo()
                .catch { }
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

    fun addFriend() {
        viewModelScope.launch {
            val searchUserId = _uiState.value.searchResult.userId ?: ""
            val searchUserProfileImage = _uiState.value.searchResult.userProfileImage

            userRepository.addFriend(searchUserId, searchUserProfileImage)
                .catch {
                    _sideEffect.emit(ProfileSideEffect.Message(message = "친구 추가에 실패하였습니다."))
                }
                .collect { isSuccessful ->
                    if (isSuccessful) {
                        _sideEffect.emit(ProfileSideEffect.ShowKeyboard(show = false))
                    } else {
                        _sideEffect.emit(ProfileSideEffect.Message(message = "친구 추가에 실패하였습니다."))
                    }
                }
        }
    }

    fun deleteFriend(userId: String) {
        viewModelScope.launch {
            userRepository.deleteFriend(userId = userId)
                .catch {
                    _sideEffect.emit(ProfileSideEffect.ShowDialog(show = false))
                    _sideEffect.emit(ProfileSideEffect.Message(message = "친구 삭제에 실패하였습니다."))
                }.collect { isSuccessful ->
                    if (isSuccessful) {
                        _sideEffect.emit(ProfileSideEffect.ShowKeyboard(show = false))
                        _sideEffect.emit(ProfileSideEffect.ShowDialog(show = false))
                    } else {
                        _sideEffect.emit(ProfileSideEffect.ShowDialog(show = false))
                        _sideEffect.emit(ProfileSideEffect.Message(message = "친구 삭제에 실패하였습니다."))
                    }
                }
        }
    }

    fun acceptFriend(friend: Friend) {
        viewModelScope.launch {
            userRepository.acceptFriend(friend.id, friend.profileImage, friend.uuid)
                .catch {
                    _sideEffect.emit(ProfileSideEffect.Message(message = "친구 추가에 실패하였습니다."))
                }
                .collect { isSuccessful ->
                    if (!isSuccessful) {
                        _sideEffect.emit(ProfileSideEffect.Message(message = "친구 추가에 실패하였습니다."))
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
                    _sideEffect.emit(ProfileSideEffect.Message(message = "친구 찾기에 실패하였습니다."))
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
                            _sideEffect.emit(ProfileSideEffect.Message(message = "친구 찾기에 실패하였습니다."))
                        }
                    }
                }
        }
    }
}