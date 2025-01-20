package com.dhkim.friend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.CommonResult
import com.dhkim.common.Dispatcher
import com.dhkim.common.RestartableStateFlow
import com.dhkim.common.TimeCapsuleDispatchers
import com.dhkim.common.onetimeRestartableStateIn
import com.dhkim.user.model.Friend
import com.dhkim.user.model.User
import com.dhkim.user.repository.UserRepository
import com.dhkim.user.usecase.AcceptFriendUseCase
import com.dhkim.user.usecase.AddFriendUseCase
import com.dhkim.user.usecase.DeleteFriendUseCase
import com.dhkim.user.usecase.CreateFriendCodeUseCase
import com.dhkim.user.usecase.GetMyInfoUseCase
import com.dhkim.user.usecase.SearchFriendUseCase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val deleteFriendUseCase: DeleteFriendUseCase,
    private val createFriendCodeUseCase: CreateFriendCodeUseCase,
    private val addFriendUseCase: AddFriendUseCase,
    private val acceptFriendUseCase: AcceptFriendUseCase,
    private val searchFriendUseCase: SearchFriendUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase,
    @Dispatcher(TimeCapsuleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val creatingCodeFlow = MutableStateFlow(false)
    private val searchResultFlow = MutableStateFlow(SearchResult())
    val uiState: RestartableStateFlow<FriendUiState> = combine(
        creatingCodeFlow,
        searchResultFlow,
        getMyInfoUseCase()
    ) { isCreating, searchResult, myInfo ->
        myInfo.toUiState(isCreatingCode = isCreating, searchResult = searchResult)
    }.onetimeRestartableStateIn(
        scope = viewModelScope,
        initialValue = FriendUiState(),
        isOnetime = false
    )

    private val _sideEffect = Channel<FriendSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    private val profileImages = listOf(
        R.drawable.ic_smile_blue,
        R.drawable.ic_smile_violet,
        R.drawable.ic_smile_green,
        R.drawable.ic_smile_orange
    )

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

            FriendAction.CreateFriendCode -> {
                createFriendCode()
            }
        }
    }

    private fun createFriendCode() {
        creatingCodeFlow.value = true
        viewModelScope.launch {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("fcm", "Fetching FCM registration token failed", task.exception)
                    creatingCodeFlow.value = false
                    return@OnCompleteListener
                }

                viewModelScope.launch {
                    val fcmToken = task.result
                    val profileImage = profileImages[(0..3).random()]
                    val isSuccessful = createFriendCodeUseCase(fcmToken = fcmToken, profileImage = profileImage).first()
                    if (isSuccessful) {
                        uiState.restart()
                    } else {
                        _sideEffect.send(FriendSideEffect.Message(message = "코드 생성에 실패하였습니다. 다시 시도해주세요."))
                    }
                    creatingCodeFlow.value = false
                }
            }).addOnFailureListener {
                viewModelScope.launch {
                    creatingCodeFlow.value = false
                    _sideEffect.send(FriendSideEffect.Message(message = "코드 생성에 실패하였습니다. 다시 시도해주세요."))
                }
            }
        }
    }

    private fun onQuery(query: String) {
        searchResultFlow.update { it.copy(query = query) }
    }

    private fun addFriend() {
        viewModelScope.launch {
            val searchUserId = uiState.value.searchResult.userId ?: ""
            val searchUserProfileImage = uiState.value.searchResult.userProfileImage
            val isSuccessful = addFriendUseCase(searchUserId, searchUserProfileImage).first()

            if (isSuccessful) {
                _sideEffect.send(FriendSideEffect.ShowKeyboard(show = false))
            } else {
                _sideEffect.send(FriendSideEffect.Message(message = "친구 추가에 실패하였습니다."))
            }
        }
    }

    private fun deleteFriend(userId: String) {
        viewModelScope.launch(ioDispatcher) {
            val isSuccessful = deleteFriendUseCase(userId).first()
            if (isSuccessful) {
                _sideEffect.send(FriendSideEffect.ShowKeyboard(show = false))
            } else {
                _sideEffect.send(FriendSideEffect.Message(message = "친구 삭제에 실패하였습니다."))
            }
            _sideEffect.send(FriendSideEffect.ShowDialog(show = false))
        }
    }

    private fun acceptFriend(friend: Friend) {
        viewModelScope.launch {
            val isSuccessful = acceptFriendUseCase(friend.id, friend.profileImage, friend.uuid).first()
            if (!isSuccessful) _sideEffect.send(FriendSideEffect.Message(message = "친구 추가에 실패하였습니다."))
        }
    }

    private fun searchUser() {
        viewModelScope.launch {
            val myId = uiState.value.myInfo.id
            val searchResult = uiState.value.searchResult
            val result = searchFriendUseCase(userId = searchResult.query).first()

            when (result) {
                is CommonResult.Success -> {
                    val user = result.data
                    searchResultFlow.update {
                        if (user != null) {
                            searchResult.copy(userId = user.id, userProfileImage = user.profileImage, isMe = searchResult.query == myId)
                        } else {
                            searchResult.copy(userId = null)
                        }
                    }
                }

                is CommonResult.Error -> {
                    _sideEffect.send(FriendSideEffect.Message(message = "친구 찾기에 실패하였습니다."))
                }
            }
        }
    }
}

fun User.toUiState(isCreatingCode: Boolean, searchResult: SearchResult): FriendUiState {
    return FriendUiState(
        isLoading = false,
        isCreatingCode = isCreatingCode,
        myInfo = this,
        searchResult = searchResult
    )
}