package com.dhkim.timecapsule.timecapsule.data.dataSource.remote

import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.common.data.di.RetrofitModule
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject

typealias isSuccessful = Boolean
typealias Uuid = String

class TimeCapsuleRemoteDataSource @Inject constructor(
    @RetrofitModule.Fcm private val api: Retrofit,
    @RetrofitModule.KakaoPush private val pushApi: Retrofit
) {

    private val pushService = pushApi.create(TimeCapsuleApi::class.java)

    suspend fun shareTimeCapsule(
        myId: String,
        myProfileImage: String,
        sharedFriends: List<Uuid>,
        openDate: String,
        content: String,
        lat: String,
        lng: String,
        address: String,
        checkLocation: Boolean
    ): CommonResult<isSuccessful> {
        val data = CustomField(
            sender = myId,
            profileImage = myProfileImage,
            openDate = openDate,
            content = content,
            lat = if (checkLocation) {
                lat
            } else {
                "0.0"
            },
            lng = if (checkLocation) {
                lng
            } else {
                "0.0"
            },
            address = if (checkLocation) {
                address
            } else {
                ""
            },
            checkLocation = checkLocation
        )
        val gson = Gson()
        val payload = PushMessage(FcmData(custom_field = data))

        val friendsJson = gson.toJson(sharedFriends)
        val payloadJson = gson.toJson(payload)

        return try {
            val result = pushService.shareTimeCapsule(
                toUserIds = friendsJson,
                body = payloadJson
            )

            if (result.isSuccessful) {
                CommonResult.Success(true)
            } else {
                CommonResult.Error(-1)
            }
        } catch (e: HttpException) {
            CommonResult.Error(e.code())
        } catch (e: Exception) {
            CommonResult.Error(-1)
        }
    }
}

data class PushMessage(
    val for_fcm: FcmData
)

data class FcmData(
    val custom_field: CustomField
)

data class CustomField(
    val sender: String,
    val profileImage: String,
    val openDate: String,
    val content: String,
    val lat: String,
    val lng: String,
    val address: String,
    val checkLocation: Boolean
)