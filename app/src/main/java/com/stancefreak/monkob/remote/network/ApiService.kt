package com.stancefreak.monkob.remote.network

import com.stancefreak.monkob.remote.model.response.BaseResponse
import com.stancefreak.monkob.remote.model.response.ServerAvgMemory
import com.stancefreak.monkob.remote.model.response.ServerStatusChange
import com.stancefreak.monkob.remote.model.response.ServerUptime
import com.stancefreak.monkob.remote.model.request.StatusChangeRequest
import com.stancefreak.monkob.remote.model.response.ServerCpuUsage
import com.stancefreak.monkob.remote.model.response.ServerCpuUsageRecord
import com.stancefreak.monkob.remote.model.response.ServerDiskUtil
import com.stancefreak.monkob.remote.model.response.ServerNetworkUtil
import com.stancefreak.monkob.remote.model.response.ServerUtilTotal
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("status")
    suspend fun getServerUptime(
    ): Response<BaseResponse<ServerUptime>>

    @POST("change-status")
    suspend fun postServerStatus(
        @Body statusChangeRequest: StatusChangeRequest
    ): Response<BaseResponse<ServerStatusChange>>

    @GET("average-memory")
    suspend fun getServerAvgMemory(
    ): Response<BaseResponse<ServerAvgMemory>>

    @GET("cpu-util/record")
    suspend fun getServerCpuUsageRecord(
    ): Response<BaseResponse<ArrayList<ServerCpuUsageRecord>>>

    @GET("cpu-util")
    suspend fun getServerCpuUsage(
    ): Response<BaseResponse<ServerCpuUsage>>

    @GET("network-util")
    suspend fun getServerNetworkUtil(
    ): Response<BaseResponse<ArrayList<ServerNetworkUtil>>>

    @GET("network-util/total")
    suspend fun getServerNetworkUtilTotal(
    ): Response<BaseResponse<ArrayList<ServerUtilTotal>>>

    @GET("disk-util")
    suspend fun getServerDiskUtil(
    ): Response<BaseResponse<ArrayList<ServerDiskUtil>>>

    @GET("disk-util/total")
    suspend fun getServerDiskUtilTotal(
    ): Response<BaseResponse<ArrayList<ServerUtilTotal>>>

}