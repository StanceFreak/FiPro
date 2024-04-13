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

class ApiHelperImpl(
    private val service: ApiService
): ApiService {
    override suspend fun getServerUptime(): Response<BaseResponse<ServerUptime>> {
        return service.getServerUptime()
    }

    override suspend fun postServerStatus(statusChangeRequest: StatusChangeRequest): Response<BaseResponse<ServerStatusChange>> {
        return service.postServerStatus(statusChangeRequest)
    }

    override suspend fun getServerAvgMemory(): Response<BaseResponse<ServerAvgMemory>> {
        return service.getServerAvgMemory()
    }

    override suspend fun getServerCpuUsageRecord(): Response<BaseResponse<ArrayList<ServerCpuUsageRecord>>> {
        return service.getServerCpuUsageRecord()
    }

    override suspend fun getServerCpuUsage(): Response<BaseResponse<ServerCpuUsage>> {
        return service.getServerCpuUsage()
    }

    override suspend fun getServerNetworkUtil(): Response<BaseResponse<ArrayList<ServerNetworkUtil>>> {
        return service.getServerNetworkUtil()
    }

    override suspend fun getServerNetworkUtilTotal(): Response<BaseResponse<ArrayList<ServerUtilTotal>>> {
        return service.getServerNetworkUtilTotal()
    }

    override suspend fun getServerDiskUtil(): Response<BaseResponse<ArrayList<ServerDiskUtil>>> {
        return service.getServerDiskUtil()
    }

    override suspend fun getServerDiskUtilTotal(): Response<BaseResponse<ArrayList<ServerUtilTotal>>> {
        return service.getServerDiskUtilTotal()
    }
}