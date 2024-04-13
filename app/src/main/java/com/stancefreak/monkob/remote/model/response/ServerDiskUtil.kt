package com.stancefreak.monkob.remote.model.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ServerDiskUtil(
    @SerializedName("time")
    val time: String = "",
    @SerializedName("device")
    val device: String = "",
    @SerializedName("direction")
    val direction: String = "",
    @SerializedName("value")
    val value: Double = 0.0
) : Parcelable