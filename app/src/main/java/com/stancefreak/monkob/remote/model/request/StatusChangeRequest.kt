package com.stancefreak.monkob.remote.model.request

import com.google.gson.annotations.SerializedName

data class StatusChangeRequest(
    @SerializedName("command")
    val command: String
)
