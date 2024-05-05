package com.stancefreak.monkob.remote.network

import com.stancefreak.monkob.remote.model.response.BaseResponse
import com.stancefreak.monkob.remote.model.response.ServerAvgMemory
import com.stancefreak.monkob.remote.model.response.ServerStatusChange
import com.stancefreak.monkob.remote.model.response.ServerUptime
import com.stancefreak.monkob.remote.model.request.StatusChangeRequest
import com.stancefreak.monkob.remote.model.response.ServerCpuUsage
import com.stancefreak.monkob.remote.model.response.ServerDiskUsage
import com.stancefreak.monkob.remote.model.response.ServerRecord
import com.stancefreak.monkob.remote.model.response.ServerPerformanceUtil
import com.stancefreak.monkob.remote.model.response.ServerUtilTotal
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

    @GET("cpu-util/record?")
    suspend fun getServerCpuUsageRecord(
        @Query("interval") interval: String
    ): Response<BaseResponse<ArrayList<ServerRecord>>>

    @GET("memory-util/record?")
    suspend fun getServerMemoryUsageRecord(
        @Query("interval") interval: String
    ): Response<BaseResponse<ArrayList<ServerRecord>>>

    @GET("net-latency/record?")
    suspend fun getServerNetLatencyRecord(
        @Query("interval") interval: String
    ): Response<BaseResponse<ArrayList<ServerRecord>>>

    @GET("cpu-util")
    suspend fun getServerCpuUsage(
    ): Response<BaseResponse<ServerCpuUsage>>

    @GET("network-util")
    suspend fun getServerNetworkUtil(
    ): Response<BaseResponse<ArrayList<ServerPerformanceUtil>>>

    @GET("network-util/total")
    suspend fun getServerNetworkUtilTotal(
    ): Response<BaseResponse<ArrayList<ServerUtilTotal>>>

    @GET("disk-util")
    suspend fun getServerDiskUtil(
    ): Response<BaseResponse<ArrayList<ServerPerformanceUtil>>>

    @GET("disk-util/total")
    suspend fun getServerDiskUtilTotal(
    ): Response<BaseResponse<ArrayList<ServerUtilTotal>>>

    @GET("disk-util/usage")
    suspend fun getServerDiskUsage(
    ): Response<BaseResponse<ServerDiskUsage>>

}