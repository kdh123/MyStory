package com.dhkim.timecapsule.timecapsule.domain

import kotlinx.coroutines.flow.Flow

interface TimeCapsuleRepository {

    suspend fun getMyAllTimeCapsule(): Flow<List<MyTimeCapsule>>
    suspend fun getMyTimeCapsule(id: String): MyTimeCapsule?
    suspend fun getMyTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<MyTimeCapsule>>
    suspend fun saveMyTimeCapsule(timeCapsule: MyTimeCapsule)
    suspend fun editMyTimeCapsule(timeCapsule: MyTimeCapsule)
    suspend fun deleteMyTimeCapsule(id: String)
    
    suspend fun getSendAllTimeCapsule(): Flow<List<SendTimeCapsule>>
    suspend fun getSendTimeCapsule(id: String): SendTimeCapsule?
    suspend fun getSendTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<SendTimeCapsule>>
    suspend fun saveSendTimeCapsule(timeCapsule: SendTimeCapsule)
    suspend fun editSendTimeCapsule(timeCapsule: SendTimeCapsule)
    suspend fun deleteSendTimeCapsule(id: String) 

    suspend fun getReceivedAllTimeCapsule(): Flow<List<ReceivedTimeCapsule>>
    suspend fun getReceivedTimeCapsule(id: String): ReceivedTimeCapsule?
    suspend fun getReceivedTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<ReceivedTimeCapsule>>
    suspend fun saveReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsule)
    suspend fun updateReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsule)
    suspend fun deleteReceivedTimeCapsule(id: String)
}