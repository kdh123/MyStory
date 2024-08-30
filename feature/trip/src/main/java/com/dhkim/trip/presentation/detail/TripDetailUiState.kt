package com.dhkim.trip.presentation.detail

import androidx.compose.runtime.Stable
import com.dhkim.common.DateUtil
import com.dhkim.trip.domain.model.TripImage
import com.dhkim.trip.domain.model.TripVideo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

typealias Year = String
typealias Month = String
typealias Day = String

@Stable
data class TripDetailUiState(
    val isLoading: Boolean = false,
    val startDate: String = "",
    val endDate: String = "",
    val selectedIndex: Int = 0,
    val images: ImmutableList<TripImage> = persistentListOf(),
    val videos: ImmutableList<TripVideo> = persistentListOf()
) {
    val tripDates = if (startDate.isNotEmpty()) {
        mutableListOf<Triple<String, String, String>>().apply {
            var date = startDate

            while (DateUtil.isBefore(date, endDate)) {
                val currentDate = DateUtil.dateAfterDays(date, 0)
                val month = if (currentDate.second.toInt() < 10) {
                    "0${currentDate.second}"
                } else {
                    currentDate.second
                }
                val day = if (currentDate.third.toInt() < 10) {
                    "0${currentDate.third}"
                } else {
                    currentDate.third
                }
                add(Triple(currentDate.first, month, day))
                val nextDate = DateUtil.dateAfterDays(date, 1)
                date = "${nextDate.first}-${nextDate.second}-${nextDate.third}"
            }
        }.mapIndexed { index, date ->
            SelectTripDate(
                isSelected = index == selectedIndex,
                date = date
            )
        }
    } else {
        listOf()
    }
}

data class SelectTripDate(
    val isSelected: Boolean = false,
    val date: Triple<Year, Month, Day> = Triple("", "", "")
)