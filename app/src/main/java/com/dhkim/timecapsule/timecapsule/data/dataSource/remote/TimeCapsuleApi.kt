package com.dhkim.timecapsule.timecapsule.data.dataSource.remote

import com.dhkim.timecapsule.BuildConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface TimeCapsuleApi {

    @POST
    suspend fun sendTimeCapsule(
        @Header("Content-type") type: String = "application/json",
        @Header("Authorization") token: String = BuildConfig.FCM_AUTHORIZATION,
        @Url url: String,
        @Body body: SendTimeCapsuleDto
    ): Response<SendTimeCapsuleResponse>

}