package com.stancefreak.monkob.remote.model.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ServerAvgMemory(
    @SerializedName("memoryAvailable")
    val memoryAvailable: Double = 0.0,
    @SerializedName("memoryTotal")
    val memoryTotal: Double = 0.0,
    @SerializedName("memoryUsage")
    val memoryUsage: Double = 0.0,
    @SerializedName("time")
    val time: String = ""
) : Parcelable