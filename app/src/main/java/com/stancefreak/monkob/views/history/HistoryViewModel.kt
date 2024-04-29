package com.stancefreak.monkob.views.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stancefreak.monkob.remote.model.response.Records
import com.stancefreak.monkob.remote.model.response.ServerRecord
import com.stancefreak.monkob.remote.repository.AppRepository
import com.stancefreak.monkob.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: AppRepository
): ViewModel() {

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
    fun observeServerRecord(): LiveData<SingleLiveEvent<ArrayList<Records>?>> = serverRecord
    fun observeApiLoading(): LiveData<SingleLiveEvent<Boolean>> = apiLoading
    fun observeApiError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = apiError

    fun getServerRecords(
        interval: String
    ) {
        viewModelScope.launch {
            apiLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val cpuRecord = repo.getServerCpuUsageRecord(interval)
                val memRecord = repo.getServerMemoryUsageRecord(interval)
                val latencyRecord = repo.getServerNetLatencyRecord(interval)

                if (cpuRecord.isSuccessful.not() || cpuRecord.body() == null) {
                    val err = cpuRecord.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                }
                if (memRecord.isSuccessful.not() || memRecord.body() == null) {
                    val err = memRecord.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                }
                if (latencyRecord.isSuccessful.not() || latencyRecord.body() == null) {
                    val err = latencyRecord.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                }
                else {
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    recordList.add(Records(cpuRecord.body()?.data))
                    recordList.add(Records(memRecord.body()?.data))
                    recordList.add(Records(latencyRecord.body()?.data))
                    serverCpuUtilsRecord.postValue(SingleLiveEvent(cpuRecord.body()?.data))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                apiLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

    fun getServerCpuUtilsRecords(
        interval: String
    ) {
        viewModelScope.launch {
            apiLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val cpuRecord = repo.getServerCpuUsageRecord(interval)

                if (cpuRecord.isSuccessful.not() || cpuRecord.body() == null) {
                    val err = cpuRecord.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                }
                else {
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverCpuUtilsRecord.postValue(SingleLiveEvent(cpuRecord.body()?.data))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                apiLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

    fun getServerMemUtilsRecords(
        interval: String
    ) {
        viewModelScope.launch {
            apiLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val memRecord = repo.getServerMemoryUsageRecord(interval)

                if (memRecord.isSuccessful.not() || memRecord.body() == null) {
                    val err = memRecord.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                }
                else {
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverMemUtilsRecord.postValue(SingleLiveEvent(memRecord.body()?.data))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                apiLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

    fun getServerNetLatencyRecords(
        interval: String
    ) {
        viewModelScope.launch {
            apiLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val latencyRecord = repo.getServerNetLatencyRecord(interval)
                if (latencyRecord.isSuccessful.not() || latencyRecord.body() == null) {
                    val err = latencyRecord.errorBody()?.string()?.let { JSONObject(it) }
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, err?.getString("message"))))
                }
                else {
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverNetLatencyRecord.postValue(SingleLiveEvent(latencyRecord.body()?.data))
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