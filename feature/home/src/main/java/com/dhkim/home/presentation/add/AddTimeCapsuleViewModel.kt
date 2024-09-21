@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.dhkim.home.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dhkim.common.CommonResult
import com.dhkim.common.DateUtil
import com.dhkim.home.domain.MyTimeCapsule
import com.dhkim.home.domain.SharedFriend
import com.dhkim.home.domain.TimeCapsuleRepository
import com.dhkim.location.domain.LocationRepository
import com.dhkim.location.domain.Place
import com.dhkim.user.domain.UserId
import com.dhkim.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _sideEffect = Channel<AddTimeCapsuleSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    private var selectImageIndex = -1

    private val query = MutableStateFlow("")
    private val checkedFriend = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(
                userRepository.getMyInfo(),
                checkedFriend
            ) { myInfo, checkedFriendId ->
                myInfo.friends.map {
                    SharedFriend(
                        isChecked = it.id == checkedFriendId,
                        userId = it.id,
                        nickname = it.nickname,
                        uuid = it.uuid
                    )
                }
            }.catch {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }.collect {
                _uiState.value =
                    _uiState.value.copy(isShare = it.any { it.isChecked }, sharedFriends = it)
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

    fun onAction(action: AddTimeCapsuleAction) {
        when (action) {
            is AddTimeCapsuleAction.AddFriend -> {
                addFriend(friendId = action.friendId)
            }

            is AddTimeCapsuleAction.AddImage -> {
                addImage(imageUrl = action.imageUrl)
            }

            is AddTimeCapsuleAction.CheckSharedFriend -> {
                checkSharedFriend(userId = action.friendId)
            }

            is AddTimeCapsuleAction.InitPlace -> {
                initPlace(place = action.place)
            }

            is AddTimeCapsuleAction.PlaceClick -> {
                onPlaceClick(place = action.place)
            }

            is AddTimeCapsuleAction.Query -> {
                onQuery(s = action.query)
            }

            AddTimeCapsuleAction.SaveTimeCapsule -> {
                saveTimeCapsule()
            }

            is AddTimeCapsuleAction.SearchAddress -> {
                searchAddress(lat = action.lat, lng = action.lng)
            }

            is AddTimeCapsuleAction.SetCheckLocation -> {
                setCheckLocation(isChecked = action.isCheck)
            }

            is AddTimeCapsuleAction.SetCheckShare -> {
                setCheckShare(isChecked = action.isCheck)
            }

            is AddTimeCapsuleAction.SetOpenDate -> {
                setOpenDate(date = action.date)
            }

            is AddTimeCapsuleAction.SetSelectImageIndex -> {
                setSelectImageIndex(index = action.index)
            }

            is AddTimeCapsuleAction.Typing -> {
                typing(str = action.text)
            }
        }
    }

    private fun initPlace(place: Place) {
        _uiState.value = _uiState.value.copy(
            lat = place.lat,
            lng = place.lng,
            placeName = place.name,
            address = place.address,
            checkLocation = true
        )
    }

    private fun addFriend(friendId: String) {
        checkedFriend.value = friendId
    }

    private fun onQuery(s: String) {
        query.value = s
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            placeQuery = s
        )
    }

    private fun onPlaceClick(place: Place) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                lat = place.lat,
                lng = place.lng,
                placeName = place.name,
                address = place.address
            )
            _sideEffect.send(AddTimeCapsuleSideEffect.ShowPlaceBottomSheet(show = false))
        }
    }

    private fun setSelectImageIndex(index: Int) {
        selectImageIndex = index
    }

    private fun searchAddress(lat: String, lng: String) {
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
                    _uiState.value = _uiState.value.copy(
                        lat = lat,
                        lng = lng,
                        placeName = "알 수 없음",
                        address = "알 수 없음"
                    )
                }
            }
        }
    }

    private fun saveTimeCapsule() {
        viewModelScope.launch(Dispatchers.IO) {
            with(_uiState.value) {
                when {
                    openDate.isEmpty() -> {
                        _sideEffect.send(AddTimeCapsuleSideEffect.Message("개봉 날짜를 선택해주세요."))
                    }

                    content.isEmpty() || content.isBlank() -> {
                        _sideEffect.send(AddTimeCapsuleSideEffect.Message("내용을 입력해주세요."))
                    }

                    isShare && sharedFriends.isEmpty() -> {
                        _sideEffect.send(AddTimeCapsuleSideEffect.Message("친구를 선택해주세요."))
                    }

                    else -> {
                        val timeCapsuleId = "${System.currentTimeMillis()}"
                        val timeCapsule = MyTimeCapsule(
                            id = timeCapsuleId,
                            date = DateUtil.todayDate(),
                            openDate = openDate,
                            lat = lat,
                            lng = lng,
                            placeName = placeName,
                            address = address,
                            images = imageUrls,
                            content = content,
                            checkLocation = checkLocation,
                            isOpened = false,
                            sharedFriends = sharedFriends.filter { it.isChecked }.map { it.userId }
                        )

                        if (sharedFriends.none { it.isChecked }) {
                            timeCapsuleRepository.saveMyTimeCapsule(timeCapsule = timeCapsule)
                            _sideEffect.send(AddTimeCapsuleSideEffect.Completed(isCompleted = true))
                        } else {
                            val isSuccessful = timeCapsuleRepository.shareTimeCapsule(
                                timeCapsuleId = timeCapsuleId,
                                sharedFriends = sharedFriends.filter { it.isChecked }
                                    .map { it.uuid },
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
                                _sideEffect.send(AddTimeCapsuleSideEffect.Completed(isCompleted = true))
                            } else {
                                _sideEffect.send(AddTimeCapsuleSideEffect.Message(message = "저장에 실패하였습니다."))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkSharedFriend(userId: UserId) {
        val sharedFriends = _uiState.value.sharedFriends.map {
            if (it.userId == userId) {
                it.copy(isChecked = !it.isChecked)
            } else {
                it
            }
        }

        _uiState.value = _uiState.value.copy(isShare = true, sharedFriends = sharedFriends)
    }

    private fun typing(str: String) {
        _uiState.value = _uiState.value.copy(content = str)
    }

    private fun addImage(imageUrl: String) {
        val currentImageUrls = _uiState.value.imageUrls.toMutableList().apply {
            if (selectImageIndex < 0) {
                add(imageUrl)
            } else {
                set(selectImageIndex, imageUrl)
            }
        }
        _uiState.value = _uiState.value.copy(imageUrls = currentImageUrls)
    }

    private fun setCheckLocation(isChecked: Boolean) {
        _uiState.value = _uiState.value.copy(checkLocation = isChecked)
    }

    private fun setCheckShare(isChecked: Boolean) {
        _uiState.value = _uiState.value.copy(isShare = isChecked)
    }

    private fun setOpenDate(date: String) {
        _uiState.value = _uiState.value.copy(openDate = date)
    }
}