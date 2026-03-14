package com.dhkim.data.dataSource.remote

import com.dhkim.common.CommonResult
import com.dhkim.domain.model.DeleteTimeCapsule
import com.dhkim.domain.model.FcmData
import com.dhkim.domain.model.PushMessage
import com.dhkim.domain.model.ShareTimeCapsuleField
import com.dhkim.network.di.RetrofitModule
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject

typealias isSuccessful = Boolean
typealias Uuid = String

internal class TimeCapsuleRemoteDataSource @Inject constructor(
    @RetrofitModule.Fcm private val api: Retrofit,
    @RetrofitModule.KakaoPush private val pushApi: Retrofit
) {

    private val pushService = pushApi.create(TimeCapsuleApi::class.java)

    suspend fun shareTimeCapsule(
        timeCapsuleId: String,
        myId: String,
        myProfileImage: String,
        sharedFriends: List<Uuid>,
        openDate: String,
        content: String,
        lat: String,
        lng: String,
        placeName: String,
        address: String,
        checkLocation: Boolean
    ): CommonResult<isSuccessful> {
        val data = ShareTimeCapsuleField(
            timeCapsuleId = timeCapsuleId,
            sender = myId,
            profileImage = myProfileImage,
            openDate = openDate,
            content = content,
            lat = if (checkLocation) lat else "0.0",
            lng = if (checkLocation) lng else "0.0",
            placeName = if (checkLocation) placeName else "",
            address = if (checkLocation) address else "",
            checkLocation = checkLocation
        )
        val gson = Gson()
        val payload = PushMessage(FcmData(custom_field = data))

        val friendsJson = gson.toJson(sharedFriends)
        val payloadJson = gson.toJson(payload)

        return try {
            val result = pushService.shareTimeCapsule(toUserIds = friendsJson, body = payloadJson)
            if (result.isSuccessful) CommonResult.Success(true) else CommonResult.Error(-1)
        } catch (e: HttpException) {
            CommonResult.Error(e.code())
        } catch (e: Exception) {
            CommonResult.Error(-1)
        }
    }

    suspend fun deleteTimeCapsule(myId: String, sharedFriends: List<Uuid>, timeCapsuleId: String): CommonResult<isSuccessful> {
        val data = DeleteTimeCapsule(isDelete = true, sender = myId, timeCapsuleId = timeCapsuleId)
        val gson = Gson()
        val payload = PushMessage(FcmData(custom_field = data))
        val friendsJson = gson.toJson(sharedFriends)
        val payloadJson = gson.toJson(payload)

        return try {
            val result = pushService.shareTimeCapsule(toUserIds = friendsJson, body = payloadJson)
            if (result.isSuccessful) CommonResult.Success(true) else CommonResult.Error(-1)
        } catch (e: HttpException) {
            CommonResult.Error(e.code())
        } catch (e: Exception) {
            CommonResult.Error(-1)
        }
    }
}

