package com.stancefreak.monkob.remote.model.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ServerRecord(
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("time")
    val time: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("value")
    val value: Double = 0.0
) : Parcelable