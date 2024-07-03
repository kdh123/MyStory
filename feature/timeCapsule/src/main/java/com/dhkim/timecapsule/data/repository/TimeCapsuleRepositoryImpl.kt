package com.dhkim.timecapsule.data.repository

import com.dhkim.common.CommonResult
import com.dhkim.timecapsule.data.dataSource.local.TimeCapsuleLocalDataSource
import com.dhkim.timecapsule.data.dataSource.remote.TimeCapsuleRemoteDataSource
import com.dhkim.timecapsule.data.dataSource.remote.Uuid
import com.dhkim.timecapsule.data.dataSource.toMyTimeCapsule
import com.dhkim.timecapsule.data.dataSource.toReceivedTimeCapsule
import com.dhkim.timecapsule.data.dataSource.toSenderTimeCapsule
import com.dhkim.timecapsule.domain.MyTimeCapsule
import com.dhkim.timecapsule.domain.ReceivedTimeCapsule
import com.dhkim.timecapsule.domain.SendTimeCapsule
import com.dhkim.timecapsule.domain.TimeCapsuleRepository
import com.dhkim.user.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

typealias isSuccessful = Boolean

class TimeCapsuleRepositoryImpl @Inject constructor(
    private val localDataSource: TimeCapsuleLocalDataSource,
    private val remoteDataSource: TimeCapsuleRemoteDataSource,
    private val userRepository: UserRepository
) : TimeCapsuleRepository {

    override suspend fun shareTimeCapsule(
        timeCapsuleId: String,
        sharedFriends: List<Uuid>,
        openDate: String,
        content: String,
        lat: String,
        lng: String,
        placeName: String,
        address: String,
        checkLocation: Boolean
    ): isSuccessful {
        val myId = userRepository.getMyId()
        val myProfileImage = "${userRepository.getProfileImage()}"

        return remoteDataSource.shareTimeCapsule(
            timeCapsuleId = timeCapsuleId,
            myId = myId,
            myProfileImage = myProfileImage,
            sharedFriends = sharedFriends,
            openDate = openDate,
            content = content,
            lat = lat,
            lng = lng,
            placeName = placeName,
            address = address,
            checkLocation = checkLocation
        ) is CommonResult.Success
    }

    override suspend fun deleteTimeCapsule(sharedFriends: List<Uuid>, timeCapsuleId: String): isSuccessful {
        val myId = userRepository.getMyId()
        return remoteDataSource.deleteTimeCapsule(myId, sharedFriends, timeCapsuleId) is CommonResult.Success
    }

    override suspend fun getMyAllTimeCapsule(): Flow<List<MyTimeCapsule>> {
        return localDataSource.getMyAllTimeCapsule().map { timeCapsules ->
            timeCapsules?.map {
                it.toMyTimeCapsule()
            } ?: listOf()
        }
    }

    override suspend fun getMyTimeCapsule(id: String): MyTimeCapsule? {
        return localDataSource.getMyTimeCapsule(id = id)?.toMyTimeCapsule()
    }

    override suspend fun getMyTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<MyTimeCapsule>> {
        return localDataSource.getMyTimeCapsulesInDate(startDate, endDate).map { timeCapsules ->
            timeCapsules?.map {
                it.toMyTimeCapsule()
            } ?: listOf()
        }
    }

    override suspend fun saveMyTimeCapsule(timeCapsule: MyTimeCapsule) {
        val entity = timeCapsule.run {
            com.dhkim.database.entity.MyTimeCapsuleEntity(
                id, date, openDate, lat, lng, placeName, address, content, medias, checkLocation, isOpened, sharedFriends
            )
        }

        localDataSource.saveMyTimeCapsule(timeCapsule = entity)
    }

    override suspend fun editMyTimeCapsule(timeCapsule: MyTimeCapsule) {
        val entity = timeCapsule.run {
            com.dhkim.database.entity.MyTimeCapsuleEntity(
                id, date, openDate, lat, lng, placeName, address, content, medias, checkLocation, isOpened, sharedFriends
            )
        }

        localDataSource.updateMyTimeCapsule(timeCapsule = entity)
    }

    override suspend fun deleteMyTimeCapsule(id: String) {
        localDataSource.deleteMyTimeCapsule(id = id)
    }

    override suspend fun getSendAllTimeCapsule(): Flow<List<SendTimeCapsule>> {
        return localDataSource.getSendAllTimeCapsule().map { timeCapsules ->
            timeCapsules?.map {
                it.toSenderTimeCapsule()
            } ?: listOf()
        }
    }

    override suspend fun getSendTimeCapsule(id: String): SendTimeCapsule? {
        return localDataSource.getSendTimeCapsule(id = id)?.toSenderTimeCapsule()
    }

    override suspend fun getSendTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<SendTimeCapsule>> {
        return localDataSource.getSendTimeCapsulesInDate(startDate, endDate).map { timeCapsules ->
            timeCapsules?.map {
                it.toSenderTimeCapsule()
            } ?: listOf()
        }
    }

    override suspend fun saveSendTimeCapsule(timeCapsule: SendTimeCapsule) {
        val entity = timeCapsule.run {
            com.dhkim.database.entity.SendTimeCapsuleEntity(
                id, date, openDate, sharedFriends, lat, lng, address, content, checkLocation, isChecked
            )
        }

        localDataSource.saveSendTimeCapsule(timeCapsule = entity)
    }

    override suspend fun editSendTimeCapsule(timeCapsule: SendTimeCapsule) {
        val entity = timeCapsule.run {
            com.dhkim.database.entity.SendTimeCapsuleEntity(
                id, date, openDate, sharedFriends, lat, lng, address, content, checkLocation, isChecked
            )
        }

        localDataSource.updateSendTimeCapsule(timeCapsule = entity)
    }

    override suspend fun deleteSendTimeCapsule(id: String) {
        localDataSource.deleteSendTimeCapsule(id = id)
    }

    override suspend fun getReceivedAllTimeCapsule(): Flow<List<ReceivedTimeCapsule>> {
        return localDataSource.getReceivedAllTimeCapsule().map { timeCapsules ->
            timeCapsules?.map {
                it.toReceivedTimeCapsule()
            } ?: listOf()
        }
    }

    override suspend fun getReceivedTimeCapsule(id: String): ReceivedTimeCapsule? {
        return localDataSource.getReceivedTimeCapsule(id = id)?.toReceivedTimeCapsule()
    }

    override suspend fun getReceivedTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<ReceivedTimeCapsule>> {
        return localDataSource.getReceivedTimeCapsulesInDate(startDate, endDate).map { timeCapsules ->
            timeCapsules?.map {
                it.toReceivedTimeCapsule()
            } ?: listOf()
        }
    }

    override suspend fun saveReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsule) {
        val entity = timeCapsule.run {
            com.dhkim.database.entity.ReceivedTimeCapsuleEntity(
                id, date, openDate, sender, profileImage, lat, lng, placeName, address, content, checkLocation, isOpened
            )
        }

        localDataSource.saveReceivedTimeCapsule(timeCapsule = entity)
    }

    override suspend fun updateReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsule) {
        val entity = timeCapsule.run {
            com.dhkim.database.entity.ReceivedTimeCapsuleEntity(
                id, date, openDate, sender, profileImage, lat, lng, placeName, address, content, checkLocation, isOpened
            )
        }

        localDataSource.updateReceivedTimeCapsule(timeCapsule = entity)
    }

    override suspend fun deleteReceivedTimeCapsule(id: String) {
        localDataSource.deleteReceivedTimeCapsule(id = id)
    }
}