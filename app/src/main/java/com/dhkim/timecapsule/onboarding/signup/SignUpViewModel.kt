@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dhkim.timecapsule.onboarding.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.profile.data.di.FirebaseModule
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @FirebaseModule.FirebaseDatabase private val database: DatabaseReference,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SignUpSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var fcmToken = ""

    init {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("fcm", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            fcmToken = task.result
            Log.e("fcm", "token $fcmToken")
        })
    }

    fun onQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun signUp() {
        viewModelScope.launch {
            if (fcmToken.isEmpty()) {
                _sideEffect.emit(SignUpSideEffect.Message(message = "로그인에 실패하였습니다."))
                return@launch
            }

            val userId = _uiState.value.query

            when {
                userId.isEmpty() || userId.isBlank() -> {
                    _uiState.value = _uiState.value.copy(errorMessage = "아이디를 입력하세요.")
                }

                containsSpecialCharacters(userId) -> {
                    _uiState.value = _uiState.value.copy(errorMessage = "특수 문자는 포함될 수 없습니다.")
                }

                userId.length < 6 -> {
                    _uiState.value = _uiState.value.copy(errorMessage = "최소 6자리 이상의 아이디를 입력해주세요.")
                }

                else -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                    val query = _uiState.value.query
                    userRepository.searchUser(query).flatMapConcat { isExist ->
                        if (isExist != null) {
                            if (isExist) {
                                _uiState.value = _uiState.value.copy(errorMessage = "이미 존재하는 아이디입니다.")
                                flowOf(false)
                            } else {
                                flowOf(userRepository.signUp(userId = query, fcmToken))
                            }
                        } else {
                            _sideEffect.emit(SignUpSideEffect.Message(message = "로그인에 실패하였습니다."))
                            flowOf(false)
                        }
                    }.catch {
                        _sideEffect.emit(SignUpSideEffect.Message(message = "로그인에 실패하였습니다."))
                    }.collect { isSignUpSuccessful ->
                        if (isSignUpSuccessful) {
                            _sideEffect.emit(SignUpSideEffect.Completed(isCompleted = true))
                        } else {
                            _sideEffect.emit(SignUpSideEffect.Message(message = "로그인에 실패하였습니다."))
                        }
                    }
                }
            }
        }
    }

    fun containsSpecialCharacters(input: String): Boolean {
        val specialCharactersPattern = Regex("[^a-zA-Z0-9 ]") // 알파벳, 숫자, 공백 제외한 모든 문자
        return specialCharactersPattern.containsMatchIn(input)
    }
}