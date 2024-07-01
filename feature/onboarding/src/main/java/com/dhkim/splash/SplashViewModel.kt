package com.dhkim.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.onboarding.R
import com.dhkim.user.domain.UserRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SplashSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

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
        checkSignedUp()
    }

    private fun checkSignedUp() {
        viewModelScope.launch {
            val isSignedUp = userRepository.getMyId().isNotEmpty()

            if (isSignedUp) {
                _sideEffect.emit(SplashSideEffect.Completed(isCompleted = true))
            } else {
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("fcm", "Fetching FCM registration token failed", task.exception)
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
                            _sideEffect.emit(SplashSideEffect.Completed(isCompleted = true))
                        } else {
                            _sideEffect.emit(SplashSideEffect.ShowPopup(message = "앱 실행에 실패하였습니다."))
                        }
                    }
                }).addOnFailureListener {
                    viewModelScope.launch {
                        _sideEffect.emit(SplashSideEffect.ShowPopup(message = "앱 실행에 실패하였습니다."))
                    }
                }
            }
        }
    }
}