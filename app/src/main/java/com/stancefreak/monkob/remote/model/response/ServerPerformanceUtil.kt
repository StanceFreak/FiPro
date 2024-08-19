package com.stancefreak.monkob.remote.model.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ServerPerformanceUtil(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("time")
    val time: String = "",
    @SerializedName("utils")
    val utils: List<Util> = listOf()
) : Parcelable