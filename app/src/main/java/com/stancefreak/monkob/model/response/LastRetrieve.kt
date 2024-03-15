package com.stancefreak.monkob.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LastRetrieve(
    val id: Int,
    val label: String,
    val query: String
): Parcelable
