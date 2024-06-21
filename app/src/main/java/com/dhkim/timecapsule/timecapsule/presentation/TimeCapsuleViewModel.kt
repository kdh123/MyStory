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
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimeCapsuleUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<TimeCapsuleSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val myId = userRepository.getMyId()
            timeCapsuleRepository.getMyAllTimeCapsule()
                .combine(timeCapsuleRepository.getReceivedAllTimeCapsule()) { myTimeCapsules, receivedTimeCapsules ->
                    myTimeCapsules.map { it.toTimeCapsule(myId) } + receivedTimeCapsules.map { it.toTimeCapsule() }
                }.catch { }
                .collect { timeCapsules ->
                    val unOpenedMyTimeCapsules = timeCapsules
                        .filter { !it.isReceived && !it.isOpened && !DateUtil.isAfter(it.openDate)}
                        .sortedBy {
                            it.openDate
                        }
                    val unOpenedReceivedTimeCapsules = timeCapsules
                        .filter { it.isReceived && !it.isOpened && !DateUtil.isAfter(it.openDate) }
                        .sortedBy {
                            it.openDate
                        }
                    val openableTimeCapsules = timeCapsules
                        .filter { (!it.isOpened && DateUtil.isAfter(strDate = it.openDate)) }
                        .sortedBy {
                            it.openDate
                        }
                    val openedTimeCapsules = timeCapsules.filter { it.isOpened }
                        .sortedByDescending {
                            it.date
                        }

                    _uiState.value = _uiState.value.copy(
                        openableTimeCapsules = openableTimeCapsules,
                        openedTimeCapsules = openedTimeCapsules,
                        unOpenedMyTimeCapsules = unOpenedMyTimeCapsules,
                        unOpenedReceivedTimeCapsules = unOpenedReceivedTimeCapsules,
                        unOpenedTimeCapsules = unOpenedMyTimeCapsules + unOpenedReceivedTimeCapsules,
                    )
                }
        }
    }

    fun deleteTimeCapsule(timeCapsuleId: String, isReceived: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isReceived) {
                timeCapsuleRepository.deleteReceivedTimeCapsule(timeCapsuleId)
            } else {
                timeCapsuleRepository.deleteMyTimeCapsule(timeCapsuleId)
            }
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