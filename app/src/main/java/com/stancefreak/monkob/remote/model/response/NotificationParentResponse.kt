package com.stancefreak.monkob.remote.model.response

data class NotificationParentResponse(
    val id: Int,
    val header: String,
    val data: List<ServerNotifRecord>
)
