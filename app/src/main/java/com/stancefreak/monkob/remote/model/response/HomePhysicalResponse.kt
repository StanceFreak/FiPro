package com.stancefreak.monkob.remote.model.response

data class HomePhysicalResponse(
    val id: Int,
    val netUtilData: ArrayList<ServerPerformanceUtil>?,
    val diskUtilData: ArrayList<ServerPerformanceUtil>?,
    val utilTotalData: ArrayList<ServerUtilTotal>?
)
