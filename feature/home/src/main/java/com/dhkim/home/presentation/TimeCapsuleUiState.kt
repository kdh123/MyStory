package com.dhkim.home.presentation

import androidx.compose.runtime.Stable
import com.dhkim.common.Constants
import com.naver.maps.geometry.LatLng
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class TimeCapsuleUiState(
    val isLoading: Boolean = true,
    val timeCapsules: ImmutableList<TimeCapsuleItem> = persistentListOf(),
    val currentLocation: LatLng = Constants.defaultLocation
)