package com.dhkim.home

import com.dhkim.database.entity.MyTimeCapsuleEntity

object FakeTimeCapsuleData {

    var myTimeCapsuleEntity2: MyTimeCapsuleEntity? = null


    fun setMyTimeCapsuleEntity(myTimeCapsuleEntity: MyTimeCapsuleEntity) {
        this.myTimeCapsuleEntity2 = myTimeCapsuleEntity
    }

    fun close() {
        myTimeCapsuleEntity2 = null
    }

}