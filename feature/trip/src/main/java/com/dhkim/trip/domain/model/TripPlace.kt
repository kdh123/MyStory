package com.dhkim.trip.domain.model

import androidx.compose.runtime.Stable

@Stable
sealed interface TripPlace {

    @Stable
    enum class DomesticPlace(val placeName: String) : TripPlace {
        Seoul("서울"),
        Incheon("인천"),
        Gyeongi("경기도"),
        Kangwon("강원도"),
        ChungCheong("충청도"),
        Daejeon("대전"),
        Sejong("세종"),
        Kyungsang("경상도"),
        Busan("부산"),
        Ulsan("울산"),
        Daegu("대구"),
        Jeonra("전라도"),
        Kwangju("광주"),
        Jeju("제주도"),
        Domestic("국내"),
    }

    @Stable
    enum class AbroadPlace(val placeName: String) : TripPlace {
        USA("미국"),
        Japan("일본"),
        Canada("캐나다"),
        France("프랑스"),
        Germany("독일"),
        Netherlands("네덜란드"),
        Swiss("스위스"),
        Italy("이탈리아"),
        Finland("핀란드"),
        Spain("스페인"),
        England("영국"),
        Sweden("스웨덴"),
        Belgium("벨기에"),
        Denmark("덴마크"),
        Poland("폴란드"),
        Greece("그리스"),
        Australia("호주"),
        China("중국"),
        Philippines("필리핀"),
        Vietnam("베트남"),
        Singapore("싱가포르"),
        Taipei("대만"),
        Thailand("태국"),
        Hongkong("홍콩"),
        India("인도"),
        Russia("러시아"),
        Mexico("멕시코"),
        Columbia("콜롬비아"),
        Brazil("브라질"),
        Argentina("아르헨티나"),
        UAE("아랍에미리트")
    }
}

enum class TripType(val type: Int, val desc: String) {
    Alone(0, "나홀로 여행"),
    Family(1, "가족 여행"),
    Lover(2, "연인과 여행"),
    Friend(3, "친구와 여행"),
    Acquaintance(4, "지인과 여행"),
    Stranger(5, "낯선 사람과 여행")
}

fun Int.toTripType(): TripType {
    return when (this) {
        0 -> TripType.Alone
        1 -> TripType.Family
        2 -> TripType.Lover
        3 -> TripType.Friend
        4 -> TripType.Acquaintance
        else -> TripType.Stranger
    }
}