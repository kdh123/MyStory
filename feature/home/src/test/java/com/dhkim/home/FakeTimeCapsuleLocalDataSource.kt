package com.dhkim.home

import com.dhkim.database.entity.MyTimeCapsuleEntity
import com.dhkim.database.entity.ReceivedTimeCapsuleEntity
import com.dhkim.database.entity.SendTimeCapsuleEntity
import com.dhkim.home.data.dataSource.local.TimeCapsuleLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FakeTimeCapsuleLocalDataSource @Inject constructor() : TimeCapsuleLocalDataSource {

    override fun getMyAllTimeCapsule(): Flow<List<MyTimeCapsuleEntity>?> {
        TODO("Not yet implemented")
    }

    override fun getMyTimeCapsule(id: String): MyTimeCapsuleEntity? {
        return FakeTimeCapsuleData.myTimeCapsuleEntity2
    }

    override fun getMyTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<MyTimeCapsuleEntity>?> {
        TODO("Not yet implemented")
    }

    override fun saveMyTimeCapsule(timeCapsule: MyTimeCapsuleEntity) {
        TODO("Not yet implemented")
    }

    override fun updateMyTimeCapsule(timeCapsule: MyTimeCapsuleEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteMyTimeCapsule(id: String) {
        TODO("Not yet implemented")
    }

    override fun getSendAllTimeCapsule(): Flow<List<SendTimeCapsuleEntity>?> {
        TODO("Not yet implemented")
    }

    override fun getSendTimeCapsule(id: String): SendTimeCapsuleEntity? {
        TODO("Not yet implemented")
    }

    override fun getSendTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<SendTimeCapsuleEntity>?> {
        TODO("Not yet implemented")
    }

    override fun saveSendTimeCapsule(timeCapsule: SendTimeCapsuleEntity) {
        TODO("Not yet implemented")
    }

    override fun updateSendTimeCapsule(timeCapsule: SendTimeCapsuleEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteSendTimeCapsule(id: String) {
        TODO("Not yet implemented")
    }

    override fun getReceivedAllTimeCapsule(): Flow<List<ReceivedTimeCapsuleEntity>?> {
        TODO("Not yet implemented")
    }

    override fun getReceivedTimeCapsule(id: String): ReceivedTimeCapsuleEntity? {
        TODO("Not yet implemented")
    }

    override fun getReceivedTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<ReceivedTimeCapsuleEntity>?> {
        TODO("Not yet implemented")
    }

    override fun saveReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsuleEntity) {
        TODO("Not yet implemented")
    }

    override fun updateReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsuleEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteReceivedTimeCapsule(id: String) {
        TODO("Not yet implemented")
    }
}