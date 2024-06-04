package com.dhkim.timecapsule.timecapsule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.common.DateUtil
import com.dhkim.timecapsule.search.domain.SearchRepository
import com.dhkim.timecapsule.timecapsule.domain.MyTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.SendTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTimeCapsuleViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTimeCapsuleUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AddTimeCapsuleSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var selectImageIndex = -1

    fun setSelectImageIndex(index: Int) {
        selectImageIndex = index
    }

    fun searchAddress(lat: String, lng: String) {
        viewModelScope.launch {
            val result = searchRepository.getAddress(lat, lng)

            when (result) {
                is CommonResult.Success -> {
                    _uiState.value = _uiState.value.copy(address = result.data?.address ?: "알 수 없음")
                }

                is CommonResult.Error -> {
                    _uiState.value = _uiState.value.copy(address = "알 수 없음")
                }
            }
        }
    }

    fun saveTimeCapsule(lat: String, lng: String) {
        viewModelScope.launch(Dispatchers.IO) {
            with(_uiState.value) {
                when {
                    openDate.isEmpty() -> {
                        _sideEffect.emit(AddTimeCapsuleSideEffect.Message("오픈 날짜를 선택해주세요."))
                    }

                    content.isEmpty() -> {
                        _sideEffect.emit(AddTimeCapsuleSideEffect.Message("내용을 입력해주세요."))
                    }

                    imageUrls.isEmpty() -> {
                        _sideEffect.emit(AddTimeCapsuleSideEffect.Message("최소 1장 이상의 사진을 등록해주세요."))
                    }

                    isSend && receiver.isEmpty() -> {
                        _sideEffect.emit(AddTimeCapsuleSideEffect.Message("친구를 선택해주세요."))
                    }

                    else -> {
                        if (isSend) {
                            val timeCapsule = SendTimeCapsule(
                                id = "${System.currentTimeMillis()}",
                                date = DateUtil.todayDate(),
                                openDate = openDate,
                                receiver = receiver,
                                lat = lat,
                                lng = lng,
                                address = address,
                                content = content,
                                checkLocation = checkLocation,
                                isChecked = false
                            )
                            timeCapsuleRepository.saveSendTimeCapsule(timeCapsule = timeCapsule)
                        } else {
                            val timeCapsule = MyTimeCapsule(
                                id = "${System.currentTimeMillis()}",
                                date = DateUtil.todayDate(),
                                openDate = openDate,
                                lat = lat,
                                lng = lng,
                                address = address,
                                medias = imageUrls,
                                content = content,
                                checkLocation = checkLocation,
                                isOpened = false
                            )
                            timeCapsuleRepository.saveMyTimeCapsule(timeCapsule = timeCapsule)
                        }
                        _sideEffect.emit(AddTimeCapsuleSideEffect.Completed(isCompleted = true))
                    }
                }
            }
        }
    }

    fun typing(str: String) {
        _uiState.value = _uiState.value.copy(content = str)
    }

    fun addImage(imageUrl: String) {
        val currentImageUrls = _uiState.value.imageUrls.toMutableList().apply {
            if (selectImageIndex < 0) {
                add(imageUrl)
            } else {
                set(selectImageIndex, imageUrl)
            }
        }
        _uiState.value = _uiState.value.copy(imageUrls = currentImageUrls)
    }

    fun setCheckLocation(isChecked: Boolean) {
        _uiState.value = _uiState.value.copy(checkLocation = isChecked)
    }

    fun setCheckSend(isChecked: Boolean) {
        _uiState.value = _uiState.value.copy(isSend = isChecked)
    }

    fun setOpenDate(date: String) {
        _uiState.value = _uiState.value.copy(openDate = date)
    }
}