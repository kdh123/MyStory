package com.dhkim.trip

import com.dhkim.common.DateUtil
import com.dhkim.core.trip.domain.model.TripPlace
import com.dhkim.core.trip.domain.model.TripType
import junit.framework.TestCase.assertEquals
import org.junit.Test

class UnitTest {

    @Test
    fun `랜덤`() {
        val userId = StringBuilder().apply {
            repeat(6) {
                when ((0..2).random()) {
                    0 -> append(('0'.code..'9'.code).random().toChar())
                    1 -> append(('A'.code..'Z'.code).random().toChar())
                    2 -> append(('a'.code..'z'.code).random().toChar())
                }
            }
        }
        println(userId)
    }

    @Test
    fun `특정 날짜 이후인지 체크`() {
        val strDate1 = "2024-05-06"
        val strDate2 = "2024-04-06"

        val isAfter = DateUtil.isAfter(strDate1, strDate2)
        assertEquals(isAfter, true)
    }

    @Test
    fun `국내 여행 여부 체크 로직`() {
        val places = listOf("서울", "부산", "미국")
        val isDomestic = places.firstOrNull { place ->
            com.dhkim.core.trip.domain.model.TripPlace.AbroadPlace.entries.map {
                it.placeName
            }.contains(place)
        } == null


        assertEquals(isDomestic, false)
    }

    @Test
    fun `다음 날짜 값 가져오기`() {
        val date = DateUtil.dateAfterDays(date = "2024-12-31", days = 0)

        println("${date.first} : ${date.second} : ${date.third}")
    }

    @Test
    fun `날짜 목록 가져오기`() {
        val startDate = "2024-03-25"
        val endDate = "2024-03-25"

        val tripDate = mutableListOf<Pair<String, String>>().apply {
            var date = startDate

            while (DateUtil.isBefore(date, endDate)) {
                val dateValue = DateUtil.dateAfterDays(date, 0)
                add(Pair(dateValue.second, dateValue.third))
                val value2 = DateUtil.dateAfterDays(date, 1)
                date = "${value2.first}-${value2.second}-${value2.third}"
            }
        }

        println(tripDate)
    }

    @Test
    fun `TripPlace 전환`() {
        val place = com.dhkim.core.trip.domain.model.TripPlace.DomesticPlace.entries.first { it.placeName == "서울" }
        println(place)
    }

    @Test
    fun `Trip Type 전환`() {
        val type = 2.toTripType()
        println("type : $type")
    }
}

fun Int.toTripType(): com.dhkim.core.trip.domain.model.TripType {
    return when (this) {
        0 -> com.dhkim.core.trip.domain.model.TripType.Alone
        1 -> com.dhkim.core.trip.domain.model.TripType.Family
        2 -> com.dhkim.core.trip.domain.model.TripType.Lover
        3 -> com.dhkim.core.trip.domain.model.TripType.Friend
        4 -> com.dhkim.core.trip.domain.model.TripType.Acquaintance
        else -> com.dhkim.core.trip.domain.model.TripType.Stranger
    }
}