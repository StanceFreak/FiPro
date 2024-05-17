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
    private val apiLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val apiError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    private val recordList = ArrayList<Records>()
    fun observeServerCpuUtilsRecord(): LiveData<SingleLiveEvent<ArrayList<ServerRecord>?>> = serverCpuUtilsRecord
    fun observeServerMemUtilsRecord(): LiveData<SingleLiveEvent<ArrayList<ServerRecord>?>> = serverMemUtilsRecord
    fun observeServerNetLatencyRecord(): LiveData<SingleLiveEvent<ArrayList<ServerRecord>?>> = serverNetLatencyRecord
    fun observeApiLoading(): LiveData<SingleLiveEvent<Boolean>> = apiLoading
    fun observeApiError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = apiError

    fun fetchCpuRecords(
        interval: String
    ) {
        viewModelScope.launch {
            fetchCpuCount++
            apiLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val cpuRecord = repo.getServerCpuUsageRecord(interval)

                fetchCpuStatus = if (cpuRecord.isSuccessful.not() || cpuRecord.body() == null) {
                    val err = cpuRecord.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    false
                } else {
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverCpuUtilsRecord.postValue(SingleLiveEvent(cpuRecord.body()?.data))
                    true
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                apiLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
                fetchCpuStatus = false
            }
        }
    }

    fun fetchMemRecords(
        interval: String
    ) {
        viewModelScope.launch {
            fetchMemCount++
            apiLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val memRecord = repo.getServerMemoryUsageRecord(interval)

                fetchMemStatus = if (memRecord.isSuccessful.not() || memRecord.body() == null) {
                    val err = memRecord.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    false
                } else {
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverMemUtilsRecord.postValue(SingleLiveEvent(memRecord.body()?.data))
                    true
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                apiLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
                fetchMemStatus = false
            }
        }
    }

    fun fetchLatencyRecords(
        interval: String
    ) {
        viewModelScope.launch {
            fetchLatencyCount++
            apiLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val latencyRecord = repo.getServerNetLatencyRecord(interval)
                fetchLatencyStatus = if (latencyRecord.isSuccessful.not() || latencyRecord.body() == null) {
                    val err = latencyRecord.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                    false
                } else {
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverNetLatencyRecord.postValue(SingleLiveEvent(latencyRecord.body()?.data))
                    true
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                apiLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
                fetchLatencyStatus = false
            }
        }
    }

}