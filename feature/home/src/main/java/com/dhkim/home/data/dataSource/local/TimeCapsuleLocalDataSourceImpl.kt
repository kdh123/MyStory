package com.dhkim.home.data.dataSource.local

import com.dhkim.database.AppDatabase
import com.dhkim.database.entity.MyTimeCapsuleEntity
import com.dhkim.database.entity.ReceivedTimeCapsuleEntity
import com.dhkim.database.entity.SendTimeCapsuleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimeCapsuleLocalDataSourceImpl @Inject constructor(
    private val db: AppDatabase
) : TimeCapsuleLocalDataSource {

    private val myTimeCapsuleService = db.myTimeCapsuleDao()
    private val sendTimeCapsuleService = db.sendTimeCapsuleDao()
    private val receivedTimeCapsuleService = db.receivedTimeCapsuleDao()

    override fun getMyAllTimeCapsule(): Flow<List<MyTimeCapsuleEntity>?> {
        return myTimeCapsuleService.getAllTimeCapsule()
    }

    override fun getMyTimeCapsule(id: String): MyTimeCapsuleEntity? {
        return myTimeCapsuleService.getTimeCapsule(id)
    }

    override fun getMyTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<MyTimeCapsuleEntity>?> {
        return myTimeCapsuleService.getTimeCapsulesInDate(startDate, endDate)
    }

    override fun saveMyTimeCapsule(timeCapsule: MyTimeCapsuleEntity) {
        return myTimeCapsuleService.saveTimeCapsule(timeCapsule)
    }

    override fun updateMyTimeCapsule(timeCapsule: MyTimeCapsuleEntity) {
        myTimeCapsuleService.updateTimeCapsule(timeCapsule)
    }

    override fun deleteMyTimeCapsule(id: String) {
        myTimeCapsuleService.deleteTimeCapsule(id)
    }



    override  fun getSendAllTimeCapsule(): Flow<List<SendTimeCapsuleEntity>?> {
        return sendTimeCapsuleService.getAllTimeCapsule()
    }

    override fun getSendTimeCapsule(id: String): SendTimeCapsuleEntity? {
        return sendTimeCapsuleService.getTimeCapsule(id)
    }

    override fun getSendTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<SendTimeCapsuleEntity>?> {
        return sendTimeCapsuleService.getTimeCapsulesInDate(startDate, endDate)
    }

    override fun saveSendTimeCapsule(timeCapsule: SendTimeCapsuleEntity) {
        return sendTimeCapsuleService.saveTimeCapsule(timeCapsule)
    }

    override fun updateSendTimeCapsule(timeCapsule: SendTimeCapsuleEntity) {
        sendTimeCapsuleService.updateTimeCapsule(timeCapsule)
    }

    override fun deleteSendTimeCapsule(id: String) {
        sendTimeCapsuleService.deleteTimeCapsule(id)
    }


    override fun getReceivedAllTimeCapsule(): Flow<List<ReceivedTimeCapsuleEntity>?> {
        return receivedTimeCapsuleService.getAllTimeCapsule()
    }

    override fun getReceivedTimeCapsule(id: String): ReceivedTimeCapsuleEntity? {
        return receivedTimeCapsuleService.getTimeCapsule(id)
    }

    override fun getReceivedTimeCapsulesInDate(startDate: String, endDate: String): Flow<List<ReceivedTimeCapsuleEntity>?> {
        return receivedTimeCapsuleService.getTimeCapsulesInDate(startDate, endDate)
    }

    override fun saveReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsuleEntity) {
        return receivedTimeCapsuleService.saveTimeCapsule(timeCapsule)
    }

    override fun updateReceivedTimeCapsule(timeCapsule: ReceivedTimeCapsuleEntity) {
        receivedTimeCapsuleService.updateTimeCapsule(timeCapsule)
    }

    override fun deleteReceivedTimeCapsule(id: String) {
        receivedTimeCapsuleService.deleteTimeCapsule(id)
    }
}