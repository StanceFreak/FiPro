package com.stancefreak.monkob.views.monitoring

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stancefreak.monkob.remote.model.response.HomePerformanceResponse
import com.stancefreak.monkob.remote.model.response.HomePhysicalResponse
import com.stancefreak.monkob.remote.model.response.ServerAvgMemory
import com.stancefreak.monkob.remote.model.response.ServerCpuUsage
import com.stancefreak.monkob.remote.model.response.ServerDiskUsage
import com.stancefreak.monkob.remote.repository.AppRepository
import com.stancefreak.monkob.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class MonitoringPhysicalViewModel @Inject constructor(
    private val repo: AppRepository
): ViewModel() {
    private var fetchStatus = true
    private var fetchCount = 0
    private val serverAvgMemory = MutableLiveData<SingleLiveEvent<ServerAvgMemory?>>()
    private val serverCpuUsage = MutableLiveData<SingleLiveEvent<ServerCpuUsage?>>()
    private val serverDiskUsage = MutableLiveData<SingleLiveEvent<ServerDiskUsage?>>()
    private val serverPhysicalUtilData = MutableLiveData<SingleLiveEvent<ArrayList<HomePhysicalResponse>?>>()
    private val serverPerformanceUtilData = MutableLiveData<SingleLiveEvent<ArrayList<HomePerformanceResponse>?>>()
    private val apiLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val apiError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    private val physicalData = ArrayList<HomePhysicalResponse>()
    private val performanceData = ArrayList<HomePerformanceResponse>()

    fun observeServerAvgMemory(): LiveData<SingleLiveEvent<ServerAvgMemory?>> = serverAvgMemory
    fun observeServerCpuUsage(): LiveData<SingleLiveEvent<ServerCpuUsage?>> = serverCpuUsage
    fun observeServerDiskUsage(): LiveData<SingleLiveEvent<ServerDiskUsage?>> = serverDiskUsage
    fun observePhysicalServerUtil(): LiveData<SingleLiveEvent<ArrayList<HomePhysicalResponse>?>> = serverPhysicalUtilData
    fun observePerformanceServerUtil(): LiveData<SingleLiveEvent<ArrayList<HomePerformanceResponse>?>> = serverPerformanceUtilData
    fun observeApiLoading(): LiveData<SingleLiveEvent<Boolean>> = apiLoading
    fun observeApiError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = apiError

    fun getPhysicalStats() {
        val firstCallTime = ceil(System.currentTimeMillis() / 60_000.0).toLong() * 60_000
        viewModelScope.launch {
            fetchData(true)
            delay(firstCallTime - System.currentTimeMillis())
            while (fetchStatus) {
                launch {
                    if (fetchCount > 0) {
                        fetchData(false)
                    }
                }
                delay(60_000)
            }
        }
    }

    private suspend fun fetchData(loading: Boolean) {
        fetchCount++
        apiLoading.postValue(SingleLiveEvent(loading))
        apiError.postValue(SingleLiveEvent(Pair(false, null)))
        try {
            val avgMemory = repo.getServerAvgMemory()
            val cpuUsage = repo.getServerCpuUsage()
            val diskUsage = repo.getServerDiskUsage()
            val netUtil = repo.getServerNetworkUtil()
            val netUtilTotal = repo.getServerNetworkUtilTotal()
            val diskUtil = repo.getServerDiskUtil()
            val diskUtilTotal = repo.getServerDiskUtilTotal()

            when {
                avgMemory.isSuccessful.not() || avgMemory.body() == null-> {
                    val err = avgMemory.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    fetchStatus = false
                }
                cpuUsage.isSuccessful.not() || cpuUsage.body() == null-> {
                    val err = cpuUsage.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    fetchStatus = false
                }
                diskUsage.isSuccessful.not() || diskUsage.body() == null-> {
                    val err = diskUsage.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    fetchStatus = false
                }
                netUtil.isSuccessful.not() || netUtil.body() == null-> {
                    val err = netUtil.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    fetchStatus = false
                }
                netUtilTotal.isSuccessful.not() || netUtilTotal.body() == null-> {
                    val err = netUtilTotal.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    fetchStatus = false
                }
                diskUtil.isSuccessful.not() || diskUtil.body() == null-> {
                    val err = diskUtil.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    fetchStatus = false
                }
                diskUtilTotal.isSuccessful.not() || avgMemory.body() == null-> {
                    val err = diskUtilTotal.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    fetchStatus = false
                }
                else -> {
                    physicalData.clear()
                    performanceData.clear()
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverAvgMemory.postValue(SingleLiveEvent(avgMemory.body()?.data))
                    serverCpuUsage.postValue(SingleLiveEvent(cpuUsage.body()?.data))
                    serverDiskUsage.postValue(SingleLiveEvent(diskUsage.body()?.data))
                    physicalData.add(HomePhysicalResponse(0, netUtil.body()?.data, null, null))
                    physicalData.add(HomePhysicalResponse(1, null, null, netUtilTotal.body()?.data))
                    physicalData.add(HomePhysicalResponse(2, null, diskUtil.body()?.data, null))
                    physicalData.add(HomePhysicalResponse(3, null, null, diskUtilTotal.body()?.data))
                    performanceData.add(HomePerformanceResponse(0, "Network I/O", netUtil.body()?.data))
                    performanceData.add(HomePerformanceResponse(1, "Disk I/O",diskUtil.body()?.data))
                    serverPhysicalUtilData.postValue(SingleLiveEvent(physicalData))
                    serverPerformanceUtilData.postValue(SingleLiveEvent(performanceData))
                    fetchStatus = true
                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            apiLoading.postValue(SingleLiveEvent(false))
            apiError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            fetchStatus = false
        }
    }

}