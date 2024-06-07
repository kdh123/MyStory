package com.dhkim.timecapsule.timecapsule.data.dataSource.remote

import com.dhkim.timecapsule.BuildConfig
import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.common.data.di.RetrofitModule
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject

typealias isSuccessful = Boolean

class TimeCapsuleRemoteDataSource @Inject constructor(
    @RetrofitModule.Fcm private val api: Retrofit
) {

    private val service = api.create(TimeCapsuleApi::class.java)

    suspend fun sendTimeCapsule(
        fcmToken: String,
        friends: List<String>,
        openDate: String,
        content: String,
        lat: String,
        lng: String,
        address: String
    ): CommonResult<isSuccessful> {
        val friend = friends[0]
        val sendTimeCapsuleData = SendTimeCapsuleData(
            id = "${System.currentTimeMillis()}",
            userId = friend,
            openDate = openDate,
            content = content,
            lat = lat,
            lng = lng,
            address = address,
            title = "${friend}님이 타임캡슐을 공유하였습니다.",
        )
        val sendTImeCapsuleMessage = SendTimeCapsuleMessage(
            token = fcmToken,
            data = sendTimeCapsuleData
        )
        val sendTimeCapsuleDto = SendTimeCapsuleDto(
            message = sendTImeCapsuleMessage
        )

        return try {
            val result = service.sendTimeCapsule(
                url = BuildConfig.FCM_URL,
                body = sendTimeCapsuleDto
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