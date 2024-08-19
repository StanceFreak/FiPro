package com.stancefreak.monkob.remote.model.request

import com.google.gson.annotations.SerializedName

data class RegisterDeviceRequest(
    @SerializedName("token")
    val token: String
)
