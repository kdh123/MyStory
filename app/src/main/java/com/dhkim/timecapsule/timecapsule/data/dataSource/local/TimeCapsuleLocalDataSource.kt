package com.dhkim.timecapsule.timecapsule.data.dataSource.local

import com.dhkim.database.AppDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimeCapsuleLocalDataSource @Inject constructor(
    private val db: AppDatabase
) {

    private val myTimeCapsuleService = db.myTimeCapsuleDao()
    private val sendTimeCapsuleService = db.sendTimeCapsuleDao()
    private val receivedTimeCapsuleService = db.receivedTimeCapsuleDao()

    suspend fun getMyAllTimeCapsule(): Flow<List<com.dhkim.database.entity.MyTimeCapsuleEntity>?> {
        return myTimeCapsuleService.getAllTimeCapsule()
    }

    fun getMyTimeCapsule(id: String): com.dhkim.database.entity.MyTimeCapsuleEntity? {
        return myTimeCapsuleService.getTimeCapsule(id)
    }

    fun getMyTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<com.dhkim.database.entity.MyTimeCapsuleEntity>?> {
        return myTimeCapsuleService.getTimeCapsulesInDate(startDate, endDate)
    }

    fun saveMyTimeCapsule(timeCapsule: com.dhkim.database.entity.MyTimeCapsuleEntity) {
        return myTimeCapsuleService.saveTimeCapsule(timeCapsule)
    }

    fun updateMyTimeCapsule(timeCapsule: com.dhkim.database.entity.MyTimeCapsuleEntity) {
        myTimeCapsuleService.updateTimeCapsule(timeCapsule)
    }

    fun deleteMyTimeCapsule(id: String) {
        myTimeCapsuleService.deleteTimeCapsule(id)
    }



    fun getSendAllTimeCapsule(): Flow<List<com.dhkim.database.entity.SendTimeCapsuleEntity>?> {
        return sendTimeCapsuleService.getAllTimeCapsule()
    }

    fun getSendTimeCapsule(id: String): com.dhkim.database.entity.SendTimeCapsuleEntity? {
        return sendTimeCapsuleService.getTimeCapsule(id)
    }

    fun getSendTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<com.dhkim.database.entity.SendTimeCapsuleEntity>?> {
        return sendTimeCapsuleService.getTimeCapsulesInDate(startDate, endDate)
    }

    fun saveSendTimeCapsule(timeCapsule: com.dhkim.database.entity.SendTimeCapsuleEntity) {
        return sendTimeCapsuleService.saveTimeCapsule(timeCapsule)
    }

    fun updateSendTimeCapsule(timeCapsule: com.dhkim.database.entity.SendTimeCapsuleEntity) {
        sendTimeCapsuleService.updateTimeCapsule(timeCapsule)
    }

    fun deleteSendTimeCapsule(id: String) {
        sendTimeCapsuleService.deleteTimeCapsule(id)
    }


    fun getReceivedAllTimeCapsule(): Flow<List<com.dhkim.database.entity.ReceivedTimeCapsuleEntity>?> {
        return receivedTimeCapsuleService.getAllTimeCapsule()
    }

    fun getReceivedTimeCapsule(id: String): com.dhkim.database.entity.ReceivedTimeCapsuleEntity? {
        return receivedTimeCapsuleService.getTimeCapsule(id)
    }

    fun getReceivedTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<com.dhkim.database.entity.ReceivedTimeCapsuleEntity>?> {
        return receivedTimeCapsuleService.getTimeCapsulesInDate(startDate, endDate)
    }

    fun saveReceivedTimeCapsule(timeCapsule: com.dhkim.database.entity.ReceivedTimeCapsuleEntity) {
        return receivedTimeCapsuleService.saveTimeCapsule(timeCapsule)
    }

    fun updateReceivedTimeCapsule(timeCapsule: com.dhkim.database.entity.ReceivedTimeCapsuleEntity) {
        receivedTimeCapsuleService.updateTimeCapsule(timeCapsule)
    }

    fun deleteReceivedTimeCapsule(id: String) {
        receivedTimeCapsuleService.deleteTimeCapsule(id)
    }
}