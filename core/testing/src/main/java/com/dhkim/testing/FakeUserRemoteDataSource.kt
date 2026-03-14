package com.dhkim.testing

import com.dhkim.common.CommonResult
import com.dhkim.data.datasource.UserRemoteDataSource
import com.dhkim.domain.model.Friend
import com.dhkim.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeUserRemoteDataSource : UserRemoteDataSource {

    private val friends = mutableListOf<Friend>().apply {
        repeat(10) {
            val friend = Friend(
                id = "id$it",
                nickname = "nickname$it",
                profileImage = "$it",
                uuid = "uuid$it"
            )
            add(friend)
        }
    }

    private var myInfo = User(
        id = "myId",
        uuid = "myUuid",
        profileImage = "1",
        friends = friends
    )

    override fun getMyInfo(myId: String): Flow<User> {
        return flowOf(myInfo)
    }

    override fun searchUser(userId: String): Flow<CommonResult<User?>> {
        return flowOf(CommonResult.Success(User()))
    }

    override fun updateUser(user: User): Flow<Boolean> {
        return flowOf(true)
    }

    override fun acceptFriend(
        myId: String,
        myProfileImage: String,
        myUuid: String,
        userId: String,
        userProfileImage: String,
        userUuid: String
    ): Flow<Boolean> {
        val friendIndex = friends.indexOfFirst { it.id == userId }
        val updateFriend = friends.find { it.id == userId }?.copy(isPending = false) ?: return flowOf(false)
        friends[friendIndex] = updateFriend
        return flowOf(true)
    }

    override fun addFriend(myId: String, myProfileImage: String, myUuid: String, userId: String, userProfileImage: String): Flow<Boolean> {
        friends.add(Friend(id = userId, profileImage = userProfileImage))
        return flowOf(true)
    }

    override fun deleteFriend(myId: String, userId: String): Flow<Boolean> {
        friends.removeIf { it.id == userId }
        return flowOf(true)
    }

    override fun updateFriend(myId: String, friend: Friend): Flow<Boolean> {
        val friendIndex = friends.indexOfFirst { it.id == friend.id }
        friends[friendIndex] = friend
        return flowOf(true)
    }

    override fun addRequest(myId: String, myProfileImage: String, userId: String): Flow<Boolean> {
        return flowOf(true)
    }

    override suspend fun registerPush(uuid: String, fcmToken: String): CommonResult<Int> {
        return CommonResult.Success(0)
    }
}