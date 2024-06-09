package com.stancefreak.monkob.views.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stancefreak.monkob.remote.model.response.Records
import com.stancefreak.monkob.remote.model.response.ServerRecord
import com.stancefreak.monkob.remote.repository.AppRepository
import com.stancefreak.monkob.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: AppRepository
): ViewModel() {
    private var fetchCpuStatus = true
    private var fetchMemStatus = true
    private var fetchLatencyStatus = true
    private var fetchCpuCount = 0
    private var fetchMemCount = 0
    private var fetchLatencyCount = 0
    private val serverCpuUtilsRecord = MutableLiveData<SingleLiveEvent<ArrayList<ServerRecord>?>>()
    private val serverMemUtilsRecord = MutableLiveData<SingleLiveEvent<ArrayList<ServerRecord>?>>()
    private val serverNetLatencyRecord = MutableLiveData<SingleLiveEvent<ArrayList<ServerRecord>?>>()
    private val serverRecord = MutableLiveData<SingleLiveEvent<ArrayList<Records>?>>()
    private val cpuLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val memLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val latencyLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val apiError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    private val recordList = ArrayList<Records>()
    fun observeServerCpuUtilsRecord(): LiveData<SingleLiveEvent<ArrayList<ServerRecord>?>> = serverCpuUtilsRecord
    fun observeServerMemUtilsRecord(): LiveData<SingleLiveEvent<ArrayList<ServerRecord>?>> = serverMemUtilsRecord
    fun observeServerNetLatencyRecord(): LiveData<SingleLiveEvent<ArrayList<ServerRecord>?>> = serverNetLatencyRecord
    fun observeCpuLoading(): LiveData<SingleLiveEvent<Boolean>> = cpuLoading
    fun observeMemLoading(): LiveData<SingleLiveEvent<Boolean>> = memLoading
    fun observeLatencyLoading(): LiveData<SingleLiveEvent<Boolean>> = latencyLoading
    fun observeApiError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = apiError

    fun fetchCpuRecords(
        interval: String
    ) {
        viewModelScope.launch {
            cpuLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val startTime = System.currentTimeMillis()
                val cpuRecord = repo.getServerCpuUsageRecord(interval)

                if (cpuRecord.isSuccessful.not() || cpuRecord.body() == null) {
                    val err = cpuRecord.errorBody()?.string()?.let { JSONObject(it) }
                    cpuLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                } else {
                    cpuLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverCpuUtilsRecord.postValue(SingleLiveEvent(cpuRecord.body()?.data))
                    val elapsedTime = System.currentTimeMillis() - startTime
                    Log.d("retrieve CPU record data", elapsedTime.toString())
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                cpuLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

    fun fetchMemRecords(
        interval: String
    ) {
        viewModelScope.launch {
            memLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val startTime = System.currentTimeMillis()
                val memRecord = repo.getServerMemoryUsageRecord(interval)

                if (memRecord.isSuccessful.not() || memRecord.body() == null) {
                    val err = memRecord.errorBody()?.string()?.let { JSONObject(it) }
                    memLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                } else {
                    memLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverMemUtilsRecord.postValue(SingleLiveEvent(memRecord.body()?.data))
                    val elapsedTime = System.currentTimeMillis() - startTime
                    Log.d("retrieve memory record data", elapsedTime.toString())
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                memLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

    fun fetchLatencyRecords(
        interval: String
    ) {
        viewModelScope.launch {
            latencyLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val startTime = System.currentTimeMillis()
                val latencyRecord = repo.getServerNetLatencyRecord(interval)
                if (latencyRecord.isSuccessful.not() || latencyRecord.body() == null) {
                    val err = latencyRecord.errorBody()?.string()?.let { JSONObject(it) }
                    latencyLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                } else {
                    latencyLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverNetLatencyRecord.postValue(SingleLiveEvent(latencyRecord.body()?.data))
                    val elapsedTime = System.currentTimeMillis() - startTime
                    Log.d("retrieve latency record data", elapsedTime.toString())
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                latencyLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

}