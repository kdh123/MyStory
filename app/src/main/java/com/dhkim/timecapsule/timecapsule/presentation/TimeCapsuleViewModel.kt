package com.dhkim.timecapsule.timecapsule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val timeCapsuleRepository: TimeCapsuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimeCapsuleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            timeCapsuleRepository.getMyAllTimeCapsule()
                .catch { }
                .collect {
                    _uiState.value = _uiState.value.copy(myTimeCapsules = it)
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            timeCapsuleRepository.getSendAllTimeCapsule()
                .catch { }
                .collect {
                    _uiState.value = _uiState.value.copy(sendTimeCapsules = it)
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            timeCapsuleRepository.getReceivedAllTimeCapsule()
                .catch { }
                .collect {
                    _uiState.value = _uiState.value.copy(receivedTimeCapsules = it)
                }
        }
    }

    fun sendTimeCapsule(
                        friends: List<String>,
                        openDate: String,
                        content: String,
                        lat: String,
                        lng: String,
                        address: String,
                        checkLocation: Boolean
    ) {
        viewModelScope.launch {
            timeCapsuleRepository.shareTimeCapsule(
                friends, openDate, content, lat, lng, address, checkLocation
            )
        }
    }

    fun onAction(action: TimeCapsuleAction) {
        viewModelScope.launch(Dispatchers.IO) {
            when (action) {
                is TimeCapsuleAction.SaveMyTimeCapsule -> {
                    timeCapsuleRepository.saveMyTimeCapsule(timeCapsule = action.timeCapsule)
                }

                is TimeCapsuleAction.EditMyTimeCapsule -> {
                    timeCapsuleRepository.editMyTimeCapsule(timeCapsule = action.timeCapsule)
                }

                is TimeCapsuleAction.DeleteMyTimeCapsule -> {
                    timeCapsuleRepository.deleteMyTimeCapsule(id = action.id)
                }


                is TimeCapsuleAction.SaveSenderTimeCapsule -> {
                    timeCapsuleRepository.saveSendTimeCapsule(timeCapsule = action.timeCapsule)
                }

                is TimeCapsuleAction.EditSenderTimeCapsule -> {
                    timeCapsuleRepository.editSendTimeCapsule(timeCapsule = action.timeCapsule)
                }

                is TimeCapsuleAction.DeleteSenderTimeCapsule -> {
                    timeCapsuleRepository.deleteSendTimeCapsule(id = action.id)
                }


                is TimeCapsuleAction.SaveReceivedTimeCapsule -> {
                    timeCapsuleRepository.saveReceivedTimeCapsule(timeCapsule = action.timeCapsule)
                }

                is TimeCapsuleAction.DeleteReceivedTimeCapsule -> {
                    timeCapsuleRepository.deleteReceivedTimeCapsule(id = action.id)
                }
            }
        }
    }
}