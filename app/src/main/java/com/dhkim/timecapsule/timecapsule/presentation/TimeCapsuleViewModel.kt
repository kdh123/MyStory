package com.dhkim.timecapsule.timecapsule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.common.DateUtil
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val timeCapsuleRepository: TimeCapsuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimeCapsuleUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<TimeCapsuleSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            timeCapsuleRepository.getMyAllTimeCapsule()
                .combine(timeCapsuleRepository.getReceivedAllTimeCapsule()) { myTimeCapsules, receivedTimeCapsules ->
                    myTimeCapsules.map { it.toTimeCapsule() } + receivedTimeCapsules.map { it.toTimeCapsule() }
                }.catch { }
                .collect { timeCapsules ->
                    val unOpenedMyTimeCapsules = timeCapsules
                        .filter { !it.isReceived && !it.isOpened }
                    val unOpenedReceivedTimeCapsules = timeCapsules
                        .filter { it.isReceived && !it.isOpened }
                    val openedTimeCapsules = timeCapsules
                        .filter { (!it.isOpened && DateUtil.isAfter(strDate = it.openDate))} + timeCapsules.filter { it.isOpened }

                    _uiState.value = _uiState.value.copy(
                        unOpenedMyTimeCapsules = unOpenedMyTimeCapsules,
                        unOpenedReceivedTimeCapsules = unOpenedReceivedTimeCapsules,
                        openedTimeCapsules = openedTimeCapsules
                    )
                }
        }
    }

    fun openTimeCapsule(timeCapsule: TimeCapsule) {
        viewModelScope.launch(Dispatchers.IO) {
            if (timeCapsule.isReceived) {
                timeCapsuleRepository.updateReceivedTimeCapsule(timeCapsule.toReceivedCapsule().copy(isOpened = true))
            } else {
                timeCapsuleRepository.editMyTimeCapsule(timeCapsule.toMyTimeCapsule().copy(isOpened = true))
            }

            _sideEffect.emit(TimeCapsuleSideEffect.NavigateToDetail(timeCapsule.id, timeCapsule.isReceived))
        }
    }

    fun shareTimeCapsule(
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