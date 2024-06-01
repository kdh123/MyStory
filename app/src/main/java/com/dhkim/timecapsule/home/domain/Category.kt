package com.dhkim.timecapsule.home.domain

import com.dhkim.timecapsule.R

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

enum class Category(val code: String, val type: String, val resId: Int) {
    Popular("Popular", "맛집", R.drawable.ic_star_red),
    Restaurant("FD6", "음식점", R.drawable.ic_restaurant_gray),
    Cafe("CE7", "카페", R.drawable.ic_cafe_orange),
    Attraction("AT4", "관광명소", R.drawable.ic_camera_green),
    Resort("AD5", "숙박", R.drawable.ic_hotel_purple),
    Culture("CT1", "문화시설", R.drawable.ic_attraction_sky_blue),
    Convenience("CS2", "편의점", R.drawable.ic_convenience_green),
    Mart("MT1", "마트", R.drawable.ic_mart_yellow),
    Subway("SW8", "지하철역", R.drawable.ic_subway_blue),
    Bank("BK9", "은행", R.drawable.ic_bank_orange),
    Hospital("HP8", "병원", R.drawable.ic_hospital_green),
    School("SC4", "학교", R.drawable.ic_shool_purple),
    Kindergarton("PS3", "유치원", R.drawable.ic_child_orange),
    None("None", "None", R.drawable.ic_time_primary)
}