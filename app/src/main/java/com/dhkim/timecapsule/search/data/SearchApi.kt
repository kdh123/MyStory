package com.dhkim.timecapsule.search.data

import com.dhkim.timecapsule.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchApi {

    @GET("keyword")
    suspend fun getPlaceByKeyword(
        @Header("Authorization") token: String = BuildConfig.API_KEY,
        @Query("query") query: String,
        @Query("y") lat: String,
        @Query("x") lng: String,
        @Query("radius") range:Int = 20000,
        @Query("page") page: Int,
        @Query("size") size: Int = 15
    ): Response<PlaceDto>

    @GET("category")
    suspend fun getPlaceByCategory(
        @Header("Authorization") token: String = BuildConfig.API_KEY,
        @Query("category_group_code") category: String,
        @Query("y") lat: String,
        @Query("x") lng: String,
        @Query("radius") range:Int = 20000,
        @Query("page") page: Int,
        @Query("size") size: Int = 15
    ): Response<PlaceDto>
}