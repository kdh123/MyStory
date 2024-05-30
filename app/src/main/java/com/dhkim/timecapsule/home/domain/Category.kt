package com.dhkim.timecapsule.home.domain

/*MT1	대형마트
CS2	편의점
PS3	어린이집, 유치원
SC4	학교
AC5	학원
PK6	주차장
OL7	주유소, 충전소
SW8	지하철역
BK9	은행
CT1	문화시설
AG2	중개업소
PO3	공공기관
AT4	관광명소
AD5	숙박
FD6	음식점
CE7	카페
HP8	병원
PM9	약국*/

enum class Category(val code: String, val type: String) {
    Restaurant("FD6", "음식점"),
    Cafe("CE7", "카페"),
    Attraction("AT4", "관광명소"),
    Resort("AD5", "숙박"),
    Culture("CT1", "문화시설"),
    Convenience("CS2", "편의점"),
    Mart("MT1", "마트"),
    Subway("SW8", "지하철역"),
    Bank("BK9", "은행"),
    Hospital("HP8", "병원"),
    School("SC4", "학교"),
    Kindergarton("PS3", "유치원"),
    None("None", "None")
}