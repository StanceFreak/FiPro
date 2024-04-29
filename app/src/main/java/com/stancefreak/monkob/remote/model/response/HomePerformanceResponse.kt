package com.stancefreak.monkob.remote.model.response

data class HomePerformanceResponse(
    val id: Int,
    val title: String,
    val utilData: ArrayList<ServerPerformanceUtil>?,
)
