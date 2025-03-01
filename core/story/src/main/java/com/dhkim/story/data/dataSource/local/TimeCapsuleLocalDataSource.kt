package com.dhkim.story.data.dataSource.local

import com.dhkim.database.entity.MyTimeCapsuleEntity
import com.dhkim.database.entity.ReceivedTimeCapsuleEntity
import com.dhkim.database.entity.SendTimeCapsuleEntity
import kotlinx.coroutines.flow.Flow

interface TimeCapsuleLocalDataSource {

    fun getMyAllTimeCapsule(): Flow<List<MyTimeCapsuleEntity>?>
    fun getMyTimeCapsule(id: String): MyTimeCapsuleEntity?
    fun getMyTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<MyTimeCapsuleEntity>?>
    fun saveMyTimeCapsule(timeCapsule: MyTimeCapsuleEntity)
    fun updateMyTimeCapsule(timeCapsule: MyTimeCapsuleEntity)
    fun deleteMyTimeCapsule(id: String)



    fun getSendAllTimeCapsule(): Flow<List<SendTimeCapsuleEntity>?>
    fun getSendTimeCapsule(id: String): SendTimeCapsuleEntity?
    fun getSendTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<SendTimeCapsuleEntity>?>
    fun saveSendTimeCapsule(timeCapsule: SendTimeCapsuleEntity)
    fun updateSendTimeCapsule(timeCapsule: SendTimeCapsuleEntity)
    fun deleteSendTimeCapsule(id: String)


    fun getReceivedAllTimeCapsule(): Flow<List<ReceivedTimeCapsuleEntity>?>
    fun getReceivedTimeCapsule(id: String): ReceivedTimeCapsuleEntity?
    fun getReceivedTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<ReceivedTimeCapsuleEntity>?>
    fun saveReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsuleEntity)
    fun updateReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsuleEntity)
    fun deleteReceivedTimeCapsule(id: String)
}