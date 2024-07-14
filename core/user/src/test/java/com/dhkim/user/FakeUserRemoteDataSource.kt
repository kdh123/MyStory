package com.dhkim.user

import com.dhkim.common.CommonResult
import com.dhkim.user.data.dataSource.UserRemoteDataSource
import com.dhkim.user.data.dataSource.isSuccessful
import com.dhkim.user.domain.Friend
import com.dhkim.user.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FakeUserRemoteDataSource @Inject constructor() : UserRemoteDataSource {

    private val rawMyFriends = mutableListOf(
        Friend(
            id = "id13",
            profileImage = "158",
            uuid = "15608465",
            isPending = false
        ),
        Friend(
            id = "id7",
            profileImage = "84561",
            uuid = "84561",
            isPending = true
        ),
    )

    private val rawMyRequests = mutableListOf<Friend>()
    private val rawMyInfo = MutableStateFlow(convertedData())

    private fun convertedData(): Map<Any, Any> {
        return mutableMapOf<Any, Any>().apply {
            val friends = mutableMapOf<Any, Any>().apply {
                rawMyFriends.forEach {
                    val info = mutableMapOf<String, Any>().apply {
                        put("id", it.id)
                        put("profileImage", it.profileImage)
                        put("uuid", it.uuid)
                        put("pending", it.isPending)
                    }
                    put(it.id, info)
                }
            }
            val requests = mutableMapOf<Any, Any>().apply {
                rawMyRequests.forEach {
                    val info = mutableMapOf<String, Any>().apply {
                        put("id", it.id)
                        put("profileImage", it.profileImage)
                        put("uuid", it.uuid)
                        put("pending", it.isPending)
                    }
                    put(it.id, info)
                }
            }

            put("id", "id0")
            put("profileImage", "1230")
            put("uuid", "1236508")
            put("friends", friends)
            put("request", requests)
        }
    }

    private val users = MutableStateFlow(mutableListOf<User>().apply {
        repeat(10) {
            add(
                if (it == 0) {
                    User(
                        id = "id$it",
                        profileImage = "1230",
                        uuid = "1236508",
                        friends = listOf(
                            Friend(
                                id = "id13",
                                profileImage = "158",
                                uuid = "15608465",
                                isPending = false
                            )
                        ),
                        requests = listOf()
                    )
                } else {
                    User(
                        id = "id$it"
                    )
                }
            )
        }
    })

    override fun getMyInfo(myId: String): Flow<User> {
        return rawMyInfo.map { data ->
            val friends = (data["friends"] as? Map<*, *>)?.values
            val requests = (data["requests"] as? Map<*, *>)?.values

            val currentFriends = friends?.map {
                val data = it as Map<*, *>

                Friend(
                    id = data["id"] as? String ?: "",
                    profileImage = data["profileImage"] as? String ?: "${R.drawable.ic_smile_blue}",
                    uuid = data["uuid"] as? String ?: "",
                    isPending = data["pending"] as? Boolean ?: true,
                )
            } ?: listOf()

            val currentRequests = requests?.map {
                val data = it as Map<*, *>

                Friend(
                    id = data["id"] as? String ?: "",
                    profileImage = data["profileImage"] as? String ?: "${R.drawable.ic_smile_blue}",
                    uuid = data["uuid"] as? String ?: "",
                    isPending = data["pending"] as? Boolean ?: true,
                )
            } ?: listOf()

            val uuid = data["uuid"] as? String ?: ""
            val profileImage = data["profileImage"] as? String ?: ""

            User(
                id = myId,
                uuid = uuid,
                profileImage = profileImage,
                friends = currentFriends,
                requests = currentRequests
            )
        }
    }

    override fun searchUser(userId: String): Flow<CommonResult<User?>> {
        val user = users.value.firstOrNull { it.id == userId }
        return flowOf(CommonResult.Success(user))
    }

    override fun updateUser(user: User): Flow<isSuccessful> {
        users.value = users.value.apply {
            add(user)
        }

        return flowOf(true)
    }

    override fun acceptFriend(
        myId: String,
        myProfileImage: String,
        myUuid: String,
        userId: String,
        userProfileImage: String,
        userUuid: String
    ): Flow<isSuccessful> {
        val index = rawMyFriends.indexOfFirst { it.id == userId }
        rawMyFriends[index] = Friend(id = userId, profileImage = userProfileImage, uuid = userUuid, isPending = false)
        rawMyInfo.value = convertedData()

        return flowOf(true)
    }

    override fun addFriend(myId: String, myProfileImage: String, myUuid: String, userId: String, userProfileImage: String): Flow<isSuccessful> {
        rawMyFriends.add(Friend(id = userId, profileImage = userProfileImage, isPending = true))
        rawMyInfo.value = convertedData()

        return flowOf(true)
    }

    override fun deleteFriend(myId: String, userId: String): Flow<isSuccessful> {
        rawMyFriends.removeIf { it.id == userId }
        rawMyInfo.value = convertedData()

        return flowOf(true)
    }

    override fun addRequest(myId: String, myProfileImage: String, userId: String): Flow<isSuccessful> {
        TODO("Not yet implemented")
    }

    override fun updateFcmToken(userId: String, uuid: String): Flow<isSuccessful> {
        return flowOf(true)
    }

    override suspend fun registerPush(uuid: String, fcmToken: String): CommonResult<Int> {
        return CommonResult.Success(0)
    }
}