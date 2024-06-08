package com.dhkim.timecapsule.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.profile.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun updateFcmToken(fcmToken: String) {
        viewModelScope.launch {
            userRepository.run {
                /*if (getFcmToken().isEmpty()) {
                    registerPush(getMyUuid(), fcmToken)
                }*/
            }
        }
    }
}