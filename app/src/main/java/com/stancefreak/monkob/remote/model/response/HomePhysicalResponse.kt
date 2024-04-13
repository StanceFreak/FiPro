package com.stancefreak.monkob.remote.model.response

data class HomePhysicalResponse(
    val id: Int,
    val netUtilData: ArrayList<ServerNetworkUtil>?,
    val diskUtilData: ArrayList<ServerDiskUtil>?,
    val utilTotalData: ArrayList<ServerUtilTotal>?
)
