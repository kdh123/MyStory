package com.dhkim.timecapsule.profile.data.dataSource

import android.util.Log
import com.dhkim.timecapsule.common.CommonResult
import com.dhkim.timecapsule.common.data.di.RetrofitModule
import com.dhkim.timecapsule.common.data.di.FirebaseModule
import com.dhkim.timecapsule.profile.domain.Friend
import com.dhkim.timecapsule.profile.domain.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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

    fun getMyInfo(myId: String): Flow<User> {
        return callbackFlow {
            val userListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data = dataSnapshot.value as? Map<*, *>
                    Log.e("data212", "data2 : $data")
                    val friends = (data?.get("friends") as? Map<*, *>)?.values
                    val requests = (data?.get("requests") as? Map<*, *>)?.values

                    val currentFriends = friends?.map {
                        val data = it as Map<*, *>

                        Friend(
                            id = data["id"] as? String ?: "",
                            uuid = data["uuid"] as? String ?: "",
                            isPending = data["pending"] as? Boolean ?: true,
                        )
                    } ?: listOf()

                    val currentRequests = requests?.map {
                        val data = it as Map<*, *>

                        Friend(
                            id = data["id"] as? String ?: "",
                            uuid = data["uuid"] as? String ?: "",
                            isPending = data["pending"] as? Boolean ?: true,
                        )
                    } ?: listOf()


                    val uuid = data?.get("uuid") as? String ?: ""
                    val profileImageUrl = data?.get("profileImageUrl") as? String ?: ""

                    val user = User(
                        id = myId,
                        uuid = uuid,
                        profileImageUrl = profileImageUrl,
                        friends = currentFriends,
                        requests = currentRequests
                    )

                    trySend(user)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    trySend(User())
                }
            }

            userDatabase.child(myId).addValueEventListener(userListener)
            awaitClose()
        }
    }

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

    fun updateUser(user: User): Flow<isSuccessful> {
        return callbackFlow {
            database.child("users").child(user.id).setValue(user).addOnSuccessListener {
                trySend(true)
            }.addOnFailureListener {
                trySend(false)
            }
            awaitClose()
        }
    }

    fun acceptFriend(myId: String, myUuid: String, userId: String, userUuid: String): Flow<isSuccessful> {
        return callbackFlow {
            val childUpdates = hashMapOf<String, Any?>(
                "/users/$myId/requests/$userId" to null,
                "/users/$myId/friends/$userId" to Friend(id = userId, uuid = userUuid, isPending = false),
                "/users/$userId/friends/$myId" to Friend(id = myId, uuid = myUuid, isPending = false)
            )

            database.updateChildren(childUpdates)
                .addOnSuccessListener {
                    trySend(true)
                }.addOnFailureListener {
                    trySend(false)
                }

            awaitClose()
        }
    }

    fun addFriend(myId: String, myUuid: String, userId: String): Flow<isSuccessful> {
        return callbackFlow {
            val childUpdates = hashMapOf<String, Any>(
                "/users/$myId/friends/$userId" to Friend(id = userId, isPending = true),
                "/users/$userId/requests/$myId" to Friend(id = myId, uuid = myUuid, isPending = true),
            )

            database.updateChildren(childUpdates)
                .addOnSuccessListener {
                    trySend(true)
                }.addOnFailureListener {
                    trySend(false)
                }

            awaitClose()
        }
    }

    fun addRequest(myId: String, userId: String): Flow<isSuccessful> {
        return callbackFlow {
            database
                .child("users")
                .child(userId)
                .child("requests")
                .child(myId)
                .setValue(Friend(id = myId, isPending = true))
            awaitClose()
        }
    }

    fun updateFcmToken(userId: String, uuid: String): Flow<isSuccessful> {
        return callbackFlow {
            database.child("users").child(userId).child("uuid").setValue(uuid)
                .addOnSuccessListener {
                    trySend(true)
                }
                .addOnFailureListener {
                    trySend(false)
                }
            awaitClose()
        }
    }

    suspend fun registerPush(uuid: String, fcmToken: String): CommonResult<Int> {
        return try {
            val deviceId = UUID.randomUUID().toString()
            Log.e("register", "uuid: $uuid, deviceId: $deviceId, fcmToken: $fcmToken")
            pushService.registerPush(uuid = uuid, deviceId = deviceId, pushToken = fcmToken).run {
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