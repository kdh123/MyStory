package com.dhkim.timecapsule.profile.data.dataSource

import android.util.Log
import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.common.data.di.RetrofitModule
import com.dhkim.timecapsule.profile.data.di.FirebaseModule
import com.dhkim.timecapsule.profile.domain.User
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.HttpException
import retrofit2.Retrofit
import java.util.UUID
import javax.inject.Inject

typealias isSuccessful = Boolean
typealias isExist = Boolean

class UserRemoteDataSource @Inject constructor(
    @RetrofitModule.KakaoPush private val pushApi: Retrofit,
    @FirebaseModule.FirebaseDatabase private val database: DatabaseReference,
    @FirebaseModule.UserFirebaseDatabase private val userDatabase: DatabaseReference
) {

    private val pushService = pushApi.create(UserApi::class.java)

    fun searchUser(userId: String): Flow<CommonResult<isExist>> {
        return callbackFlow {
            database.child("users").child(userId).get().addOnSuccessListener { data ->
                data.value?.let {
                    trySend(CommonResult.Success(true))
                } ?: kotlin.run {
                    trySend(CommonResult.Success(false))
                }
            }.addOnFailureListener {
                trySend(CommonResult.Error(-1))
            }
            awaitClose()
        }
    }

    fun updateUser(user: User) {
        database.child("users").child(user.id).setValue(user)
    }

    fun updateFcmToken(userId: String, fcmToken: String): Flow<isSuccessful> {
        return callbackFlow {
            database.child("users").child(userId).child("fcmToken").setValue(fcmToken)
                .addOnSuccessListener {
                    trySend(true)
                }
                .addOnFailureListener {
                    trySend(false)
                }
            awaitClose()
        }
    }

    suspend fun registerPush(fcmToken: String): CommonResult<Int> {
        return try {
            val deviceId = UUID.randomUUID().toString()
            Log.e("deviceId", "id : $deviceId")
            pushService.registerPush(uuid = "8245", deviceId = deviceId, pushToken = fcmToken).run {
                if (isSuccessful) {
                    CommonResult.Success(body() ?: -1)
                } else {
                    CommonResult.Error(-1)
                }
            }
        } catch (e: HttpException) {
            CommonResult.Error(-1)
        } catch (e: Exception) {
            CommonResult.Error(-1)
        }
    }
}