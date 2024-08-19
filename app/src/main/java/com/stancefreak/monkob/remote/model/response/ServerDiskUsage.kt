package com.stancefreak.monkob.remote.model.response


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ServerDiskUsage(
    @SerializedName("diskSizeTotal")
    val diskSizeTotal: Long = 0,
    @SerializedName("time")
    val time: String = "",
    @SerializedName("usagePercentage")
    val usagePercentage: Int = 0,
    @SerializedName("usageSizeByte")
    val usageSizeByte: Long = 0
) : Parcelable