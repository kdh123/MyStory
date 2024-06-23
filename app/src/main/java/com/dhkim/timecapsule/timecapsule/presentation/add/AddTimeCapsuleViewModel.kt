@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.dhkim.timecapsule.timecapsule.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.common.DateUtil
import com.dhkim.timecapsule.profile.domain.UserId
import com.dhkim.timecapsule.profile.domain.UserRepository
import com.dhkim.timecapsule.location.domain.Place
import com.dhkim.timecapsule.location.domain.LocationRepository
import com.dhkim.timecapsule.timecapsule.domain.MyTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.SharedFriend
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTimeCapsuleViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
    private val locationRepository: LocationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTimeCapsuleUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AddTimeCapsuleSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var selectImageIndex = -1

    private val query = MutableStateFlow("")

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

        viewModelScope.launch {
            query.debounce(1000L).flatMapLatest {
                locationRepository.getPlaceByKeyword(
                    query = it
                )
            }.cachedIn(viewModelScope)
                .catch { }
                .collectLatest { result ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        placeResult = flowOf(result).stateIn(viewModelScope)
                    )
                }
        }
    }

    fun initPlace(place: Place) {
        _uiState.value = _uiState.value.copy(
            lat = place.lat,
            lng = place.lng,
            placeName = place.name,
            address = place.address,
            checkLocation = true
        )
    }

    fun onQuery(s: String) {
        query.value = s
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            placeQuery = s
        )
    }

    fun onPlaceClick(place: Place) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                lat = place.lat,
                lng = place.lng,
                placeName = place.name,
                address = place.address
            )
            _sideEffect.emit(AddTimeCapsuleSideEffect.ShowPlaceBottomSheet(show = false))
        }
    }

    fun setSelectImageIndex(index: Int) {
        selectImageIndex = index
    }

    fun searchAddress(lat: String, lng: String) {
        viewModelScope.launch {
            val result = locationRepository.getAddress(lat, lng)

            when (result) {
                is CommonResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        lat = lat,
                        lng = lng,
                        placeName = result.data?.placeName ?: "알 수 없음",
                        address = result.data?.placeName ?: "알 수 없음"
                    )
                }

                is CommonResult.Error -> {
                    _uiState.value = _uiState.value.copy(lat = lat, lng = lng, placeName = "알 수 없음", address = "알 수 없음")
                }
            }
        }
    }

    fun saveTimeCapsule() {
        viewModelScope.launch(Dispatchers.IO) {
            with(_uiState.value) {
                when {
                    openDate.isEmpty() -> {
                        _sideEffect.emit(AddTimeCapsuleSideEffect.Message("개봉 날짜를 선택해주세요."))
                    }

                    content.isEmpty() || content.isBlank() -> {
                        _sideEffect.emit(AddTimeCapsuleSideEffect.Message("내용을 입력해주세요."))
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
                            placeName = placeName,
                            address = address,
                            medias = imageUrls,
                            content = content,
                            checkLocation = checkLocation,
                            isOpened = false,
                            sharedFriends = sharedFriends.filter { it.isChecked }.map { it.userId }
                        )

                        if (sharedFriends.none { it.isChecked }) {
                            timeCapsuleRepository.saveMyTimeCapsule(timeCapsule = timeCapsule)
                            _sideEffect.emit(AddTimeCapsuleSideEffect.Completed(isCompleted = true))
                        } else {
                            val isSuccessful = timeCapsuleRepository.shareTimeCapsule(
                                sharedFriends = sharedFriends.filter { it.isChecked }.map { it.uuid },
                                openDate = openDate,
                                content = content,
                                lat = lat,
                                lng = lng,
                                placeName = placeName,
                                address = address,
                                checkLocation = checkLocation
                            )

                            if (isSuccessful) {
                                timeCapsuleRepository.saveMyTimeCapsule(timeCapsule = timeCapsule)
                                _sideEffect.emit(AddTimeCapsuleSideEffect.Completed(isCompleted = true))
                            } else {
                                _sideEffect.emit(AddTimeCapsuleSideEffect.Message(message = "저장에 실패하였습니다."))
                            }
                        }
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