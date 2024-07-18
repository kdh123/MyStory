package com.dhkim.friend.presentation.changeInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.user.domain.Friend
import com.dhkim.user.domain.LocalFriend
import com.dhkim.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeFriendInfoViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangeFriendInfoUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ChangeFriendInfoSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun initInfo(friend: Friend) {
        _uiState.value = _uiState.value.copy(friend = friend)
    }

    fun editFriendInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val nickname = _uiState.value.friend.nickname

            when {
                nickname.isEmpty() || nickname.isBlank() -> {
                    _sideEffect.emit(ChangeFriendInfoSideEffect.Message("닉네임을 입력해주세요."))
                }

                containSpace(nickname) -> {
                    _sideEffect.emit(ChangeFriendInfoSideEffect.Message("공백을 포함할 수 없습니다."))
                }

                else -> {
                    userRepository.updateFriend(_uiState.value.friend)
                    _sideEffect.emit(ChangeFriendInfoSideEffect.Completed(isCompleted = true))
                }
            }
        }
    }

    fun onEdit(str: String) {
        val friend = _uiState.value.friend
        _uiState.value = _uiState.value.copy(friend = friend.copy(nickname = str))
    }

    private fun containSpace(input: String): Boolean {
        return !input.matches(Regex("\\S+"))
    }
}