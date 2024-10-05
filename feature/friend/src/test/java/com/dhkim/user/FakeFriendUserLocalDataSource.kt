package com.dhkim.user

import com.dhkim.database.entity.FriendEntity
import com.dhkim.user.datasource.UserLocalDataSource
import com.dhkim.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class FakeFriendUserLocalDataSource @Inject constructor() : UserLocalDataSource {

    private val friends = MutableStateFlow<MutableList<FriendEntity>>(mutableListOf())
    private var myInfo = User(id = "id0", profileImage = "2131230936")
    private var fcmToken = ""


    override fun getAllFriend(): Flow<List<FriendEntity>?> {
        return friends
    }

    override fun getFriend(id: String): FriendEntity? {
        return friends.value.firstOrNull { it.id == id }
    }

    override fun saveFriend(friendEntity: FriendEntity) {
        friends.value = friends.value.apply {
            add(friendEntity)
        }
    }

    override fun updateFriend(friendEntity: FriendEntity) {
        val index = friends.value.indexOfFirst { it.id == friendEntity.id }
        friends.value = friends.value.apply {
            set(index, friendEntity)
        }
    }

    override fun deleteFriend(id: String) {
        friends.value = friends.value.filter { it.id == id }.toMutableList()
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