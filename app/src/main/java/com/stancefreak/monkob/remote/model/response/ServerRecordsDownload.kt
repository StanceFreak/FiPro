package com.stancefreak.monkob.remote.model.response

import com.google.gson.annotations.SerializedName

data class ServerRecordsDownload(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: String,
)
