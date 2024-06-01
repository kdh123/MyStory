package com.dhkim.timecapsule.timecapsule.data.source

import com.dhkim.timecapsule.common.data.room.AppDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimeCapsuleLocalDataSource @Inject constructor(
    private val db: AppDatabase
) {

    private val myTimeCapsuleService = db.myTimeCapsuleDao()
    private val sendTimeCapsuleService = db.sendTimeCapsuleDao()
    private val receivedTimeCapsuleService = db.receivedTimeCapsuleDao()

    suspend fun getMyAllTimeCapsule(): Flow<List<MyTimeCapsuleEntity>?> {
        return myTimeCapsuleService.getAllTimeCapsule()
    }

    fun getMyTimeCapsule(id: String): MyTimeCapsuleEntity? {
        return myTimeCapsuleService.getTimeCapsule(id)
    }

    fun getMyTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<MyTimeCapsuleEntity>?> {
        return myTimeCapsuleService.getTimeCapsulesInDate(startDate, endDate)
    }

    fun saveMyTimeCapsule(timeCapsule: MyTimeCapsuleEntity) {
        return myTimeCapsuleService.saveTimeCapsule(timeCapsule)
    }


    fun updateMyTimeCapsule(timeCapsule: MyTimeCapsuleEntity) {
        myTimeCapsuleService.updateTimeCapsule(timeCapsule)
    }

    fun deleteMyTimeCapsule(id: String) {
        myTimeCapsuleService.deleteTimeCapsule(id)
    }



    fun getSendAllTimeCapsule(): Flow<List<SendTimeCapsuleEntity>?> {
        return sendTimeCapsuleService.getAllTimeCapsule()
    }

    fun getSendTimeCapsule(id: String): SendTimeCapsuleEntity? {
        return sendTimeCapsuleService.getTimeCapsule(id)
    }

    fun getSendTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<SendTimeCapsuleEntity>?> {
        return sendTimeCapsuleService.getTimeCapsulesInDate(startDate, endDate)
    }

    fun saveSendTimeCapsule(timeCapsule: SendTimeCapsuleEntity) {
        return sendTimeCapsuleService.saveTimeCapsule(timeCapsule)
    }

    fun updateSendTimeCapsule(timeCapsule: SendTimeCapsuleEntity) {
        sendTimeCapsuleService.updateTimeCapsule(timeCapsule)
    }

    fun deleteSendTimeCapsule(id: String) {
        sendTimeCapsuleService.deleteTimeCapsule(id)
    }


    fun getReceivedAllTimeCapsule(): Flow<List<ReceivedTimeCapsuleEntity>?> {
        return receivedTimeCapsuleService.getAllTimeCapsule()
    }

    fun getReceivedTimeCapsule(id: String): ReceivedTimeCapsuleEntity? {
        return receivedTimeCapsuleService.getTimeCapsule(id)
    }

    fun getReceivedTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<ReceivedTimeCapsuleEntity>?> {
        return receivedTimeCapsuleService.getTimeCapsulesInDate(startDate, endDate)
    }

    fun saveReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsuleEntity) {
        return receivedTimeCapsuleService.saveTimeCapsule(timeCapsule)
    }

    fun updateReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsuleEntity) {
        receivedTimeCapsuleService.updateTimeCapsule(timeCapsule)
    }

    fun deleteReceivedTimeCapsule(id: String) {
        receivedTimeCapsuleService.deleteTimeCapsule(id)
    }
}