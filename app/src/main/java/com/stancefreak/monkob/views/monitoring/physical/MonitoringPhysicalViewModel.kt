package com.stancefreak.monkob.views.monitoring.physical

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stancefreak.monkob.remote.model.response.HomePhysicalResponse
import com.stancefreak.monkob.remote.model.response.ServerAvgMemory
import com.stancefreak.monkob.remote.model.response.ServerCpuUsage
import com.stancefreak.monkob.remote.model.response.ServerDiskUtil
import com.stancefreak.monkob.remote.model.response.ServerNetworkUtil
import com.stancefreak.monkob.remote.model.response.ServerUtilTotal
import com.stancefreak.monkob.remote.repository.AppRepository
import com.stancefreak.monkob.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class MonitoringPhysicalViewModel @Inject constructor(
    private val repo: AppRepository
): ViewModel() {

    private val serverAvgMemory = MutableLiveData<SingleLiveEvent<ServerAvgMemory?>>()
    private val serverCpuUsage = MutableLiveData<SingleLiveEvent<ServerCpuUsage?>>()
    private val serverNetworkUtil = MutableLiveData<SingleLiveEvent<ArrayList<ServerNetworkUtil?>>>()
    private val serverNetworkUtilTotal = MutableLiveData<SingleLiveEvent<ArrayList<ServerUtilTotal?>>>()
    private val serverDiskUtil = MutableLiveData<SingleLiveEvent<ArrayList<ServerDiskUtil?>>>()
    private val serverDiskUtilTotal = MutableLiveData<SingleLiveEvent<ArrayList<ServerUtilTotal?>>>()
    private val serverUtilData = MutableLiveData<SingleLiveEvent<ArrayList<HomePhysicalResponse>?>>()
    private val apiLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val apiError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    private val physicalData = ArrayList<HomePhysicalResponse>()

    fun observeServerAvgMemory(): LiveData<SingleLiveEvent<ServerAvgMemory?>> = serverAvgMemory
    fun observeServerCpuUsage(): LiveData<SingleLiveEvent<ServerCpuUsage?>> = serverCpuUsage
    fun observeServerUtil(): LiveData<SingleLiveEvent<ArrayList<HomePhysicalResponse>?>> = serverUtilData
    fun observeServerNetworkUtil(): LiveData<SingleLiveEvent<ArrayList<ServerNetworkUtil?>>> = serverNetworkUtil
    fun observeServerNetworkUtilTotal(): LiveData<SingleLiveEvent<ArrayList<ServerUtilTotal?>>> = serverNetworkUtilTotal
    fun observeServerDiskUtil(): LiveData<SingleLiveEvent<ArrayList<ServerDiskUtil?>>> = serverDiskUtil
    fun observeServerDiskUtilTotal(): LiveData<SingleLiveEvent<ArrayList<ServerUtilTotal?>>> = serverDiskUtilTotal
    fun observeApiLoading(): LiveData<SingleLiveEvent<Boolean>> = apiLoading
    fun observeApiError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = apiError

    fun getPhysicalStats() {
        viewModelScope.launch {
            apiLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val avgMemory = repo.getServerAvgMemory()
                val cpuUsage = repo.getServerCpuUsage()
                val netUtil = repo.getServerNetworkUtil()
                val netUtilTotal = repo.getServerNetworkUtilTotal()
                val diskUtil = repo.getServerDiskUtil()
                val diskUtilTotal = repo.getServerDiskUtilTotal()

                when {
                    avgMemory.isSuccessful.not() || avgMemory.body() == null-> {
                        val err = avgMemory.errorBody()?.string()?.let { JSONObject(it) }
                        apiLoading.postValue(SingleLiveEvent(false))
                        apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    }
                    cpuUsage.isSuccessful.not() || cpuUsage.body() == null-> {
                        val err = cpuUsage.errorBody()?.string()?.let { JSONObject(it) }
                        apiLoading.postValue(SingleLiveEvent(false))
                        apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    }
                    netUtil.isSuccessful.not() || netUtil.body() == null-> {
                        val err = netUtil.errorBody()?.string()?.let { JSONObject(it) }
                        apiLoading.postValue(SingleLiveEvent(false))
                        apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    }
                    netUtilTotal.isSuccessful.not() || netUtilTotal.body() == null-> {
                        val err = netUtilTotal.errorBody()?.string()?.let { JSONObject(it) }
                        apiLoading.postValue(SingleLiveEvent(false))
                        apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    }
                    diskUtil.isSuccessful.not() || diskUtil.body() == null-> {
                        val err = diskUtil.errorBody()?.string()?.let { JSONObject(it) }
                        apiLoading.postValue(SingleLiveEvent(false))
                        apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    }
                    diskUtilTotal.isSuccessful.not() || avgMemory.body() == null-> {
                        val err = diskUtilTotal.errorBody()?.string()?.let { JSONObject(it) }
                        apiLoading.postValue(SingleLiveEvent(false))
                        apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    }
                    else -> {
                        apiLoading.postValue(SingleLiveEvent(false))
                        apiError.postValue(SingleLiveEvent(Pair(false, null)))
                        serverAvgMemory.postValue(SingleLiveEvent(avgMemory.body()?.data))
                        serverCpuUsage.postValue(SingleLiveEvent(cpuUsage.body()?.data))
                        physicalData.add(HomePhysicalResponse(0, netUtil.body()?.data, null, null))
                        physicalData.add(HomePhysicalResponse(1, null, null, netUtilTotal.body()?.data))
                        physicalData.add(HomePhysicalResponse(2, null, diskUtil.body()?.data, null))
                        physicalData.add(HomePhysicalResponse(3, null, null, diskUtilTotal.body()?.data))
                        serverUtilData.postValue(SingleLiveEvent(physicalData))
//                        serverNetworkUtil.postValue(SingleLiveEvent())
//                        serverNetworkUtilTotal.postValue(SingleLiveEvent())
//                        serverDiskUtil.postValue(SingleLiveEvent())
//                        serverDiskUtilTotal.postValue(SingleLiveEvent())
                    }
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                apiLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

}