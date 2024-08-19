package com.stancefreak.monkob.remote.model.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ServerUtilTotal(
    @SerializedName("direction")
    val direction: String = "",
    @SerializedName("time")
    val time: String = "",
    @SerializedName("value")
    val value: Double = 0.0
) : Parcelable