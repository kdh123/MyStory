package com.dhkim.friend.changeInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.Dispatcher
import com.dhkim.common.TimeCapsuleDispatchers
import com.dhkim.user.domain.model.Friend
import com.dhkim.user.domain.repository.UserRepository
import com.dhkim.user.domain.usecase.UpdateFriendInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ChangeFriendInfoViewModel @Inject constructor(
    private val updateFriendInfoUseCase: UpdateFriendInfoUseCase,
    @Dispatcher(TimeCapsuleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangeFriendInfoUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<ChangeFriendInfoSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun initInfo(friend: Friend) {
        _uiState.update { it.copy(friend = friend) }
    }

    fun editFriendInfo() {
        viewModelScope.launch(ioDispatcher) {
            val nickname = _uiState.value.friend.nickname

            when {
                nickname.isEmpty() || nickname.isBlank() -> {
                    _sideEffect.send(ChangeFriendInfoSideEffect.Message("닉네임을 입력해주세요."))
                }

                containSpace(nickname) -> {
                    _sideEffect.send(ChangeFriendInfoSideEffect.Message("공백을 포함할 수 없습니다."))
                }

                else -> {
                    val isSuccessful = updateFriendInfoUseCase(_uiState.value.friend).first()
                    if (isSuccessful) {
                        _sideEffect.send(ChangeFriendInfoSideEffect.Completed(isCompleted = true))

                    } else {
                        _sideEffect.send(ChangeFriendInfoSideEffect.Message("친구 정보를 수정하지 못했습니다."))
                    }
                }
            }
        }
    }

    fun onEdit(str: String) {
        val friend = _uiState.value.friend
        _uiState.update { it.copy(friend = friend.copy(nickname = str)) }
    }

    private fun containSpace(input: String): Boolean {
        return !input.matches(Regex("\\S+"))
    }
}