package com.dhkim.timecapsule.timecapsule.data.dataSource.remote

import com.dhkim.timecapsule.BuildConfig
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface TimeCapsuleApi {

    @FormUrlEncoded
    @POST("send")
    suspend fun shareTimeCapsule(
        @Header("Authorization") token: String = BuildConfig.KAKAO_ADMIN_KEY,
        @Field("uuids") toUserIds: String,
        @Field("push_message") body: String
    ): Response<Unit>

}