package com.dhkim.timecapsule.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isSignedUp = MutableStateFlow<Boolean?>(null)
    val isSignUp = _isSignedUp.asStateFlow()

    fun checkSignedUp() {
        viewModelScope.launch {
            _isSignedUp.value = userRepository.getMyId().isNotEmpty()
        }
    }
}