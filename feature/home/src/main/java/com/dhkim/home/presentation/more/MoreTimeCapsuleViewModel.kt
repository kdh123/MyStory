package com.dhkim.home.presentation.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.home.domain.TimeCapsuleRepository
import com.dhkim.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreTimeCapsuleViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoreTimeCapsuleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val myId = userRepository.getMyId()
            val myProfileImage = "${userRepository.getProfileImage()}"
            timeCapsuleRepository.getMyAllTimeCapsule()
                .combine(timeCapsuleRepository.getReceivedAllTimeCapsule()) { myTimeCapsules, receivedTimeCapsules ->
                    myTimeCapsules.map {
                        val sharedFriends = it.sharedFriends.map { userId ->
                            userRepository.getFriend(userId)?.nickname ?: userId
                        }
                        it.toTimeCapsule(myId, myProfileImage, sharedFriends)
                    } + receivedTimeCapsules.map {
                        val nickname = userRepository.getFriend(it.sender)?.id ?: it.sender
                        it.toTimeCapsule(nickname)
                    }
                }.catch { }
                .collect { timeCapsules ->
                    val openedTimeCapsules = timeCapsules.filter { it.isOpened }
                        .sortedByDescending {
                            it.date
                        }

                    _uiState.value = _uiState.value.copy(timeCapsules = openedTimeCapsules)
                }
        }
    }
}