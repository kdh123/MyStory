package com.dhkim.home.presentation

import com.dhkim.home.domain.MyTimeCapsule
import com.dhkim.home.domain.ReceivedTimeCapsule
import com.dhkim.home.domain.SendTimeCapsule

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