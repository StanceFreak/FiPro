package com.stancefreak.monkob.remote.model.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ServerStatus(
    @SerializedName("up")
    val up: String = ""
) : Parcelable