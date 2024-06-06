package com.dhkim.timecapsule.onboarding.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.profile.data.di.FirebaseModule
import com.dhkim.timecapsule.profile.domain.User
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun onQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun signUp() {
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
                database.child("users").child(userId).get().addOnSuccessListener { data ->
                    if (data.value == null) {
                        val user = User().copy(id = userId)
                        database.child("users").child(userId).setValue(user)
                            .addOnSuccessListener {
                                viewModelScope.launch {
                                    userRepository.signUp(userId)
                                    _sideEffect.emit(SignUpSideEffect.Completed(isCompleted = true))
                                }
                            }
                            .addOnFailureListener {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = "다시 시도해주세요."
                                )
                            }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "동일한 아이디가 존재합니다."
                        )
                    }
                }.addOnFailureListener {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "다시 시도해주세요."
                    )
                }
            }
        }
    }

    fun containsSpecialCharacters(input: String): Boolean {
        val specialCharactersPattern = Regex("[^a-zA-Z0-9 ]") // 알파벳, 숫자, 공백 제외한 모든 문자
        return specialCharactersPattern.containsMatchIn(input)
    }
}