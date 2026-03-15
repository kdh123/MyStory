package com.dhkim.testing

import com.dhkim.data.dataSource.toMyTimeCapsule
import com.dhkim.data.dataSource.toReceivedTimeCapsule
import com.dhkim.database.entity.MyTimeCapsuleEntity
import com.dhkim.database.entity.ReceivedTimeCapsuleEntity
import com.dhkim.domain.model.MyTimeCapsule
import com.dhkim.domain.model.ReceivedTimeCapsule
import com.dhkim.domain.model.SendTimeCapsule
import com.dhkim.domain.repository.TimeCapsuleRepository
import com.dhkim.domain.repository.isSuccessful
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FakeTimeCapsuleRepository : TimeCapsuleRepository {

    private val myTimeCapsules = MutableStateFlow(mutableListOf<MyTimeCapsuleEntity>().apply {
        repeat(10) {
            val openDate = if (it % 2 == 0) {
                "2024-06-07"
            } else {
                "2026-09-12"
            }

            val isOpened = it % 4 == 0

            val myTimeCapsuleEntity = MyTimeCapsuleEntity(
                id = "id$it",
                date = "2024-07-07",
                openDate = openDate,
                lat = "0.0",
                lng = "0.0",
                placeName = "광화문$it",
                address = "서울시 어딘가$it",
                content = "안녕하세요$it",
                images = listOf(),
                videos = listOf(),
                checkLocation = false,
                isOpened = isOpened,
                sharedFriends = listOf()
            )
            add(myTimeCapsuleEntity)
        }
    })

    private val receivedTimeCapsules = MutableStateFlow(mutableListOf<ReceivedTimeCapsuleEntity>().apply {
        repeat(10) {
            val openDate = if (it % 2 == 0) {
                "2024-06-07"
            } else {
                "2026-09-12"
            }

            val receivedTimeCapsuleEntity = ReceivedTimeCapsuleEntity(
                id = "receivedId$it",
                date = "2024-06-07",
                openDate = openDate,
                lat = "0.0",
                lng = "0.0",
                placeName = "광화문$it",
                address = "서울시 어딘가$it",
                content = "안녕하세요$it",
                checkLocation = false,
                isOpened = false,
                sender = "이름$it",
                profileImage = "0"
            )
            add(receivedTimeCapsuleEntity)
        }
    })

    override fun shareTimeCapsule(
        myId: String,
        myProfileImage: String,
        timeCapsuleId: String,
        sharedFriends: List<String>,
        openDate: String,
        content: String,
        lat: String,
        lng: String,
        placeName: String,
        address: String,
        checkLocation: Boolean
    ): Flow<isSuccessful> {
        return flowOf(true)
    }

    override suspend fun deleteTimeCapsule(myId: String, sharedFriends: List<String>, timeCapsuleId: String): isSuccessful {
        return true
    }

    override fun getMyAllTimeCapsule(): Flow<List<MyTimeCapsule>> {
        return myTimeCapsules.map { timeCapsules ->
            timeCapsules.map {
                it.toMyTimeCapsule()
            }
        }
    }

    override fun getMyTimeCapsule(id: String): MyTimeCapsule? {
        return myTimeCapsules.value.firstOrNull { it.id == id }?.toMyTimeCapsule()
    }

    override suspend fun getMyTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<MyTimeCapsule>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveMyTimeCapsule(timeCapsule: MyTimeCapsule) {
        val entity = timeCapsule.run {
            MyTimeCapsuleEntity(
                id, date, openDate, lat, lng, placeName, address, content, images, videos, checkLocation, isOpened, sharedFriends
            )
        }

        val data = myTimeCapsules.value.apply {
            add(entity)
        }

        myTimeCapsules.value = data
    }

    override suspend fun editMyTimeCapsule(timeCapsule: MyTimeCapsule) {
        val entity = timeCapsule.run {
            MyTimeCapsuleEntity(
                id, date, openDate, lat, lng, placeName, address, content, images, videos, checkLocation, isOpened, sharedFriends
            )
        }

        val index = myTimeCapsules.value.indexOfFirst { it.id == timeCapsule.id }
        val data = myTimeCapsules.value.apply {
            set(index, entity)
        }
        myTimeCapsules.value = data
    }

    override suspend fun deleteMyTimeCapsule(id: String) {
        val data = myTimeCapsules.value.apply {
            removeIf { it.id == id }
        }
        myTimeCapsules.value = data
    }

    override suspend fun getReceivedTimeCapsule(id: String): ReceivedTimeCapsule? {
        return receivedTimeCapsules.value.firstOrNull { it.id == id }?.toReceivedTimeCapsule()
    }

    override suspend fun getReceivedTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<ReceivedTimeCapsule>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsule) {
        val entity = timeCapsule.run {
            ReceivedTimeCapsuleEntity(
                id, date, openDate, sender, profileImage, lat, lng, placeName, address, content, checkLocation, isOpened
            )
        }

        val data = receivedTimeCapsules.value.apply {
            add(entity)
        }
        receivedTimeCapsules.value = data
    }

    override suspend fun updateReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsule) {
        val entity = timeCapsule.run {
            ReceivedTimeCapsuleEntity(
                id, date, openDate, sender, profileImage, lat, lng, placeName, address, content, checkLocation, isOpened
            )
        }

        val index = receivedTimeCapsules.value.indexOfFirst { it.id == timeCapsule.id }
        val data = receivedTimeCapsules.value.apply {
            set(index, entity)
        }
        receivedTimeCapsules.value = data
    }

    override suspend fun deleteReceivedTimeCapsule(id: String) {
        val data = receivedTimeCapsules.value.toMutableList().apply {
            removeIf { it.id == id }
        }
        receivedTimeCapsules.value = data
    }


    override suspend fun getSendAllTimeCapsule(): Flow<List<SendTimeCapsule>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSendTimeCapsule(id: String): SendTimeCapsule? {
        TODO("Not yet implemented")
    }

    override suspend fun getSendTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<SendTimeCapsule>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveSendTimeCapsule(timeCapsule: SendTimeCapsule) {
        TODO("Not yet implemented")
    }

    override suspend fun editSendTimeCapsule(timeCapsule: SendTimeCapsule) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSendTimeCapsule(id: String) {
        TODO("Not yet implemented")
    }

    override fun getReceivedAllTimeCapsule(): Flow<List<ReceivedTimeCapsule>> {
        return receivedTimeCapsules.map { timeCapsules ->
            timeCapsules.map {
                it.toReceivedTimeCapsule()
            }
        }
    }
}