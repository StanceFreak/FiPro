package com.stancefreak.monkob.remote.model.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ServerNotifRecord(
    @SerializedName("body")
    val body: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("time")
    val time: String = "",
    @SerializedName("title")
    val title: String = ""
) : Parcelable