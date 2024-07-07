package com.dhkim.home

import com.dhkim.common.Constants
import com.dhkim.home.data.dataSource.local.TimeCapsuleLocalDataSource
import com.dhkim.home.data.dataSource.remote.Uuid
import com.dhkim.home.data.dataSource.toMyTimeCapsule
import com.dhkim.home.domain.MyTimeCapsule
import com.dhkim.home.domain.ReceivedTimeCapsule
import com.dhkim.home.domain.SendTimeCapsule
import com.dhkim.home.domain.TimeCapsuleRepository
import com.dhkim.home.domain.isSuccessful
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FakeTimeCapsuleRepository @Inject constructor(
    private val timeCapsuleLocalDataSource: TimeCapsuleLocalDataSource
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
        return true
    }

    override suspend fun deleteTimeCapsule(sharedFriends: List<Uuid>, timeCapsuleId: String): isSuccessful {
        return true
    }

    override suspend fun getMyAllTimeCapsule(): Flow<List<MyTimeCapsule>> {
        return flowOf()
    }

    override suspend fun getMyTimeCapsule(id: String): MyTimeCapsule? {
        return timeCapsuleLocalDataSource.getMyTimeCapsule(id)?.toMyTimeCapsule()
    }

    override suspend fun getMyTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<MyTimeCapsule>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveMyTimeCapsule(timeCapsule: MyTimeCapsule) {
        TODO("Not yet implemented")
    }

    override suspend fun editMyTimeCapsule(timeCapsule: MyTimeCapsule) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMyTimeCapsule(id: String) {
        TODO("Not yet implemented")
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

    override suspend fun getReceivedAllTimeCapsule(): Flow<List<ReceivedTimeCapsule>> {
        TODO("Not yet implemented")
    }

    override suspend fun getReceivedTimeCapsule(id: String): ReceivedTimeCapsule? {
        TODO("Not yet implemented")
    }

    override suspend fun getReceivedTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<ReceivedTimeCapsule>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsule) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsule) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReceivedTimeCapsule(id: String) {
        TODO("Not yet implemented")
    }
}