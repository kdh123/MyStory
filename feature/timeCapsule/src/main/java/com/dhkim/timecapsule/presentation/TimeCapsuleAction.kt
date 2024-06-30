package com.dhkim.timecapsule.presentation

import com.dhkim.timecapsule.domain.MyTimeCapsule
import com.dhkim.timecapsule.domain.ReceivedTimeCapsule
import com.dhkim.timecapsule.domain.SendTimeCapsule

sealed interface TimeCapsuleAction {

    data class SaveMyTimeCapsule(val timeCapsule: MyTimeCapsule): TimeCapsuleAction
    data class EditMyTimeCapsule(val timeCapsule: MyTimeCapsule): TimeCapsuleAction
    data class DeleteMyTimeCapsule(val id: String): TimeCapsuleAction

    data class SaveSenderTimeCapsule(val timeCapsule: SendTimeCapsule): TimeCapsuleAction
    data class EditSenderTimeCapsule(val timeCapsule: SendTimeCapsule): TimeCapsuleAction
    data class DeleteSenderTimeCapsule(val id: String): TimeCapsuleAction

    data class SaveReceivedTimeCapsule(val timeCapsule: ReceivedTimeCapsule): TimeCapsuleAction
    data class DeleteReceivedTimeCapsule(val id: String): TimeCapsuleAction
}