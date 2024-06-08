package com.dhkim.timecapsule.timecapsule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.common.DateUtil
import com.dhkim.timecapsule.profile.domain.UserId
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.dhkim.timecapsule.search.domain.SearchRepository
import com.dhkim.timecapsule.timecapsule.domain.MyTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.SharedFriend
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTimeCapsuleViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val searchRepository: SearchRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTimeCapsuleUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AddTimeCapsuleSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var selectImageIndex = -1

    init {
        viewModelScope.launch {
            userRepository.getMyInfo().catch { }
                .collect { user ->
                    val sharedFriends = user.friends
                        .filter { !it.isPending }
                        .map {
                            SharedFriend(
                                userId = it.id,
                                uuid = it.uuid
                            )
                        }
                    _uiState.value = _uiState.value.copy(sharedFriends = sharedFriends)
                }
        }
    }

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

                    content.isEmpty() || content.isBlank() -> {
                        _sideEffect.emit(AddTimeCapsuleSideEffect.Message("내용을 입력해주세요."))
                    }

                    imageUrls.isEmpty() -> {
                        _sideEffect.emit(AddTimeCapsuleSideEffect.Message("최소 1장 이상의 사진을 등록해주세요."))
                    }

                    isShare && sharedFriends.isEmpty() -> {
                        _sideEffect.emit(AddTimeCapsuleSideEffect.Message("친구를 선택해주세요."))
                    }

                    else -> {
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
                            isOpened = false,
                            sharedFriends = sharedFriends.map { it.userId }
                        )

                        timeCapsuleRepository.saveMyTimeCapsule(timeCapsule = timeCapsule)
                        _sideEffect.emit(AddTimeCapsuleSideEffect.Completed(isCompleted = true))
                    }
                }
            }
        }
    }

    fun checkSharedFriend(userId: UserId) {
        val sharedFriends = _uiState.value.sharedFriends.map {
            if (it.userId == userId) {
                it.copy(isChecked = !it.isChecked)
            } else {
                it
            }
        }

        _uiState.value = _uiState.value.copy(sharedFriends = sharedFriends)
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
        _uiState.value = _uiState.value.copy(isShare = isChecked)
    }

    fun setOpenDate(date: String) {
        _uiState.value = _uiState.value.copy(openDate = date)
    }
}