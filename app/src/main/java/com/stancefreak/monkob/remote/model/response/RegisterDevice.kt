package com.stancefreak.monkob.remote.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterDevice(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
): Parcelable
