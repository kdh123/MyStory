package com.dhkim.trip

import com.dhkim.common.DateUtil
import com.dhkim.trip.domain.model.TripPlace
import junit.framework.TestCase.assertEquals
import org.junit.Test

class UnitTest {

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
            TripPlace.AbroadPlace.entries.map {
                it.placeName
            }.contains(place)
        } == null


        assertEquals(isDomestic, false)
    }
}