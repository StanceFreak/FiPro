package com.stancefreak.monkob.remote.repository

import com.stancefreak.monkob.remote.model.request.StatusChangeRequest
import com.stancefreak.monkob.remote.model.response.BaseResponse
import com.stancefreak.monkob.remote.model.response.ServerAvgMemory
import com.stancefreak.monkob.remote.model.response.ServerCpuUsage
import com.stancefreak.monkob.remote.model.response.ServerDiskUsage
import com.stancefreak.monkob.remote.model.response.ServerRecord
import com.stancefreak.monkob.remote.model.response.ServerPerformanceUtil
import com.stancefreak.monkob.remote.model.response.ServerUtilTotal
import com.stancefreak.monkob.remote.model.response.ServerStatusChange
import com.stancefreak.monkob.remote.model.response.ServerUptime
import com.stancefreak.monkob.remote.network.ApiHelperImpl
import retrofit2.Response
import javax.inject.Inject

class AppRepository @Inject constructor(private val helperImpl: ApiHelperImpl) {

    suspend fun getServerUptime(): Response<BaseResponse<ServerUptime>> {
        return helperImpl.getServerUptime()
    }
    suspend fun postServerStatus(statusChangeRequest: StatusChangeRequest): Response<BaseResponse<ServerStatusChange>> {
        return helperImpl.postServerStatus(statusChangeRequest)
    }
    suspend fun getServerAvgMemory(): Response<BaseResponse<ServerAvgMemory>> {
        return helperImpl.getServerAvgMemory()
    }
    suspend fun getServerCpuUsageRecord(interval: String): Response<BaseResponse<ArrayList<ServerRecord>>> {
        return helperImpl.getServerCpuUsageRecord(interval)
    }
    suspend fun getServerMemoryUsageRecord(interval: String): Response<BaseResponse<ArrayList<ServerRecord>>> {
        return helperImpl.getServerMemoryUsageRecord(interval)
    }
    suspend fun getServerNetLatencyRecord(interval: String): Response<BaseResponse<ArrayList<ServerRecord>>> {
        return helperImpl.getServerNetLatencyRecord(interval)
    }
    suspend fun getServerCpuUsage(): Response<BaseResponse<ServerCpuUsage>> {
        return helperImpl.getServerCpuUsage()
    }
    suspend fun getServerNetworkUtil(): Response<BaseResponse<ArrayList<ServerPerformanceUtil>>> {
        return helperImpl.getServerNetworkUtil()
    }
    suspend fun getServerNetworkUtilTotal(): Response<BaseResponse<ArrayList<ServerUtilTotal>>> {
        return helperImpl.getServerNetworkUtilTotal()
    }
    suspend fun getServerDiskUtil(): Response<BaseResponse<ArrayList<ServerPerformanceUtil>>> {
        return helperImpl.getServerDiskUtil()
    }
    suspend fun getServerDiskUtilTotal(): Response<BaseResponse<ArrayList<ServerUtilTotal>>> {
        return helperImpl.getServerDiskUtilTotal()
    }
    suspend fun getServerDiskUsage(): Response<BaseResponse<ServerDiskUsage>> {
        return helperImpl.getServerDiskUsage()
    }

}