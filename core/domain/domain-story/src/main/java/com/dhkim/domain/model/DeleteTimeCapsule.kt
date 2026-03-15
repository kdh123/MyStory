package com.dhkim.domain.model

data class DeleteTimeCapsule(
    val sender: String = "",
    val isDelete: Boolean = false,
    val timeCapsuleId: String = ""
): CustomField