package com.dhkim.home.data

import com.dhkim.database.entity.FriendEntity
import com.dhkim.user.datasource.UserLocalDataSource
import com.dhkim.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FakeUserLocalDataSource @Inject constructor() : UserLocalDataSource {

    private var myInfo = User(
        id = "myId",
        uuid = "myUuid",
        profileImage = "2132130937"
    )

    private var fcmToken = "fcmToken"

    private val friends = mutableListOf<FriendEntity>().apply {
        repeat(10) {
            val friendEntity = FriendEntity(
                id = "id$it",
                nickname = "nickname$it",
                profileImage = "2132130937",
                uuid = "uuid$it"
            )
            add(friendEntity)
        }
    }

    override fun getAllFriend(): Flow<List<FriendEntity>?> {
        return flowOf(friends)
    }

    override fun getFriend(id: String): FriendEntity? {
        return friends.firstOrNull { it.id == id }
    }

    override fun saveFriend(friendEntity: FriendEntity) {
        friends.add(friendEntity)
    }

    override fun updateFriend(friendEntity: FriendEntity) {
        val index = friends.indexOfFirst { it.id == friendEntity.id }
        friends[index] = friendEntity
    }

    override fun deleteFriend(id: String) {
        friends.removeIf { it.id == id }
    }

    override suspend fun getUserId(): String {
        return myInfo.id
    }

    override suspend fun updateUserId(userId: String) {
        myInfo = myInfo.copy(id = userId)
    }

    override suspend fun getProfileImage(): Int {
        return myInfo.profileImage.toInt()
    }

    override suspend fun updateProfileImage(profileImage: String) {
        myInfo = myInfo.copy(profileImage = profileImage)
    }

    override suspend fun getUuid(): String {
        return myInfo.uuid
    }

    override suspend fun updateUuid(uuid: String) {
        myInfo = myInfo.copy(uuid = uuid)
    }

    override suspend fun getFcmToken(): String {
        return fcmToken
    }

    override suspend fun updateFcmToken(fcmToken: String) {
        this.fcmToken = fcmToken
    }
}