package com.stancefreak.monkob.remote.model.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Util(
    @SerializedName("direction")
    val direction: String = "",
    @SerializedName("packets")
    val packets: String = "",
    @SerializedName("value")
    val value: String = ""
) : Parcelable