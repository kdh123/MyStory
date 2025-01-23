@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.dhkim.home.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dhkim.common.CommonResult
import com.dhkim.common.Dispatcher
import com.dhkim.common.TimeCapsuleDispatchers
import com.dhkim.location.domain.model.Place
import com.dhkim.location.domain.usecase.GetAddressUseCase
import com.dhkim.location.domain.usecase.GetPlacesByKeywordUseCase
import com.dhkim.story.domain.model.SharedFriend
import com.dhkim.story.domain.usecase.SaveMyTimeCapsuleUseCase
import com.dhkim.user.model.UserId
import com.dhkim.user.usecase.GetMyInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTimeCapsuleViewModel @Inject constructor(
    private val saveMyTimeCapsuleUseCase: SaveMyTimeCapsuleUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val getPlacesByKeywordUseCase: GetPlacesByKeywordUseCase,
    private val getAddressUseCase: GetAddressUseCase,
    @Dispatcher(TimeCapsuleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTimeCapsuleUiState())
    val uiState = _uiState.onStart {
        init()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AddTimeCapsuleUiState()
    )

    private val _sideEffect = Channel<AddTimeCapsuleSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    private var selectImageIndex = -1

    private val query = MutableStateFlow("")
    private val checkedFriend = MutableStateFlow("")

    private fun init() {
        viewModelScope.launch {
            combine(getMyInfoUseCase(), checkedFriend) { myInfo, checkedFriendId ->
                myInfo.friends.map {
                    SharedFriend(
                        isChecked = it.id == checkedFriendId,
                        userId = it.id,
                        nickname = it.nickname,
                        uuid = it.uuid
                    )
                }
            }.catch {
                _uiState.update { it.copy(isLoading = false) }
            }.collect { sharedFriends ->
                _uiState.update { it.copy(isShare = sharedFriends.any { it.isChecked }, sharedFriends = sharedFriends) }
            }
        }

        viewModelScope.launch {
            query.debounce(1_000)
                .flatMapLatest { getPlacesByKeywordUseCase(query = it) }
                .cachedIn(viewModelScope)
                .catch { }
                .collectLatest { result ->
                    _uiState.update { it.copy(isLoading = false, placeResult = flowOf(result).stateIn(viewModelScope)) }
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
        _uiState.update {
            it.copy(
                lat = place.lat,
                lng = place.lng,
                placeName = place.name,
                address = place.address,
                checkLocation = true
            )
        }
    }

    private fun addFriend(friendId: String) {
        checkedFriend.update { friendId }
    }

    private fun onQuery(s: String) {
        query.update { s }
        _uiState.update { it.copy(isLoading = true, placeQuery = s) }
    }

    private fun onPlaceClick(place: Place) {
        viewModelScope.launch {
            _uiState.update { it.copy(lat = place.lat, lng = place.lng, placeName = place.name, address = place.address) }
            _sideEffect.send(AddTimeCapsuleSideEffect.ShowPlaceBottomSheet(show = false))
        }
    }

    private fun setSelectImageIndex(index: Int) {
        selectImageIndex = index
    }

    private fun searchAddress(lat: String, lng: String) {
        viewModelScope.launch {
            when (val result = getAddressUseCase(lat, lng).first()) {
                is CommonResult.Success -> {
                    _uiState.update {
                        it.copy(
                            lat = lat,
                            lng = lng,
                            placeName = result.data?.placeName ?: "알 수 없음",
                            address = result.data?.placeName ?: "알 수 없음"
                        )
                    }
                }

                is CommonResult.Error -> {
                    _uiState.update { it.copy(lat = lat, lng = lng, placeName = "알 수 없음", address = "알 수 없음") }
                }
            }
        }
    }

    private fun saveTimeCapsule() {
        viewModelScope.launch(ioDispatcher) {
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
                        val isShare = !sharedFriends.none { it.isChecked }
                        val isSuccessful = saveMyTimeCapsuleUseCase(
                            imageUrls = imageUrls,
                            sharedFriends = sharedFriends.filter { it.isChecked },
                            openDate = openDate,
                            content = content,
                            lat = lat,
                            lng = lng,
                            placeName = placeName,
                            address = address,
                            checkLocation = checkLocation,
                            isShare = isShare
                        ).first()
                        if (isSuccessful) {
                            _sideEffect.send(AddTimeCapsuleSideEffect.Completed(isCompleted = true))
                        } else {
                            _sideEffect.send(AddTimeCapsuleSideEffect.Message(message = "저장에 실패하였습니다."))
                        }
                    }
                }
            }
        }
    }

    private fun checkSharedFriend(userId: UserId) {
        val sharedFriends = _uiState.value.sharedFriends.map {
            if (it.userId == userId) it.copy(isChecked = !it.isChecked) else it
        }
        _uiState.update { it.copy(isShare = true, sharedFriends = sharedFriends) }
    }

    private fun typing(str: String) {
        _uiState.update { it.copy(content = str) }
    }

    private fun addImage(imageUrl: String) {
        val currentImageUrls = _uiState.value.imageUrls.toMutableList().apply {
            if (selectImageIndex < 0) {
                add(imageUrl)
            } else {
                set(selectImageIndex, imageUrl)
            }
        }
        _uiState.update { it.copy(imageUrls = currentImageUrls) }
    }

    private fun setCheckLocation(isChecked: Boolean) {
        _uiState.update { it.copy(checkLocation = isChecked) }
    }

    private fun setCheckShare(isChecked: Boolean) {
        _uiState.update { it.copy(isShare = isChecked) }
    }

    private fun setOpenDate(date: String) {
        _uiState.update { it.copy(openDate = date) }
    }
}