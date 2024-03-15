package com.stancefreak.monkob.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChartType(
    val id: Int,
    val type: String,
    val lastRetrieve: ArrayList<LastRetrieve>
) : Parcelable
