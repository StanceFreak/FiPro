package com.stancefreak.monkob.remote.model.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ServerCpuUsage(
    @SerializedName("time")
    val time: String = "",
    @SerializedName("value")
    val value: String = ""
) : Parcelable