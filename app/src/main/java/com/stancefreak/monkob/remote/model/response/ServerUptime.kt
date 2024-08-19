package com.stancefreak.monkob.remote.model.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ServerUptime(
    @SerializedName("up")
    val up: Int,
    @SerializedName("uptime")
    val uptime: String = ""
) : Parcelable