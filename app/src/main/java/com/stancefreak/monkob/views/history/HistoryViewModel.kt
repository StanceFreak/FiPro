package com.stancefreak.monkob.views.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stancefreak.monkob.remote.model.response.Records
import com.stancefreak.monkob.remote.model.response.ServerRecord
import com.stancefreak.monkob.remote.model.response.ServerRecordsDownload
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
    private val serverDownloadCpuRecords = MutableLiveData<SingleLiveEvent<ServerRecordsDownload?>>()
    private val serverDownloadMemRecords = MutableLiveData<SingleLiveEvent<ServerRecordsDownload?>>()
    private val serverDownloadLatRecords = MutableLiveData<SingleLiveEvent<ServerRecordsDownload?>>()
    private val serverRecord = MutableLiveData<SingleLiveEvent<ArrayList<Records>?>>()
    private val cpuLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val memLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val latencyLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val downloadLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val cpuError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    private val memError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    private val latError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    private val downloadError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    private val recordList = ArrayList<Records>()
    fun observeServerCpuUtilsRecord(): LiveData<SingleLiveEvent<ArrayList<ServerRecord>?>> = serverCpuUtilsRecord
    fun observeServerMemUtilsRecord(): LiveData<SingleLiveEvent<ArrayList<ServerRecord>?>> = serverMemUtilsRecord
    fun observeServerNetLatencyRecord(): LiveData<SingleLiveEvent<ArrayList<ServerRecord>?>> = serverNetLatencyRecord
    fun observeServerDownloadCpuRecords(): LiveData<SingleLiveEvent<ServerRecordsDownload?>> = serverDownloadCpuRecords
    fun observeServerDownloadMemRecords(): LiveData<SingleLiveEvent<ServerRecordsDownload?>> = serverDownloadMemRecords
    fun observeServerDownloadLatRecords(): LiveData<SingleLiveEvent<ServerRecordsDownload?>> = serverDownloadLatRecords
    fun observeCpuLoading(): LiveData<SingleLiveEvent<Boolean>> = cpuLoading
    fun observeMemLoading(): LiveData<SingleLiveEvent<Boolean>> = memLoading
    fun observeLatencyLoading(): LiveData<SingleLiveEvent<Boolean>> = latencyLoading
    fun observeDownloadLoading(): LiveData<SingleLiveEvent<Boolean>> = downloadLoading
    fun observeCpuError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = cpuError
    fun observeMemError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = memError
    fun observeLatError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = latError
    fun observeDownloadError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = downloadError

    fun fetchCpuRecords(
        interval: String
    ) {
        viewModelScope.launch {
            cpuLoading.postValue(SingleLiveEvent(true))
            cpuError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val cpuRecord = repo.getServerCpuUsageRecord(interval)

                if (cpuRecord.isSuccessful.not() || cpuRecord.body() == null) {
                    val err = cpuRecord.errorBody()?.string()?.let { JSONObject(it) }
                    cpuLoading.postValue(SingleLiveEvent(false))
                    val errorMsg = err?.getString("error")
                    if (errorMsg != null) {
                        cpuError.postValue(SingleLiveEvent(Pair(true, errorMsg)))
                    }
                } else {
                    cpuLoading.postValue(SingleLiveEvent(false))
                    cpuError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverCpuUtilsRecord.postValue(SingleLiveEvent(cpuRecord.body()?.data))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                cpuLoading.postValue(SingleLiveEvent(false))
                cpuError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

    fun fetchMemRecords(
        interval: String
    ) {
        viewModelScope.launch {
            memLoading.postValue(SingleLiveEvent(true))
            memError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val memRecord = repo.getServerMemoryUsageRecord(interval)

                if (memRecord.isSuccessful.not() || memRecord.body() == null) {
                    val err = memRecord.errorBody()?.string()?.let { JSONObject(it) }
                    memLoading.postValue(SingleLiveEvent(false))
                    val errorMsg = err?.getString("error")
                    if (errorMsg != null) {
                        memError.postValue(SingleLiveEvent(Pair(true, errorMsg)))
                    }
                } else {
                    memLoading.postValue(SingleLiveEvent(false))
                    memError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverMemUtilsRecord.postValue(SingleLiveEvent(memRecord.body()?.data))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                memLoading.postValue(SingleLiveEvent(false))
                memError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

    fun fetchLatencyRecords(
        interval: String
    ) {
        viewModelScope.launch {
            latencyLoading.postValue(SingleLiveEvent(true))
            latError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val latencyRecord = repo.getServerNetLatencyRecord(interval)
                if (latencyRecord.isSuccessful.not() || latencyRecord.body() == null) {
                    val err = latencyRecord.errorBody()?.string()?.let { JSONObject(it) }
                    latencyLoading.postValue(SingleLiveEvent(false))
                    val errorMsg = err?.getString("error")
                    if (errorMsg != null) {
                        latError.postValue(SingleLiveEvent(Pair(true, errorMsg)))
                    }
                } else {
                    latencyLoading.postValue(SingleLiveEvent(false))
                    latError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverNetLatencyRecord.postValue(SingleLiveEvent(latencyRecord.body()?.data))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                latencyLoading.postValue(SingleLiveEvent(false))
                latError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

    fun downloadCpuRecords(
        interval: String
    ) {
        viewModelScope.launch {
            downloadLoading.postValue(SingleLiveEvent(true))
            downloadError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val downloadRecord = repo.downloadCpuRecords(interval)
                if (downloadRecord.isSuccessful.not() || downloadRecord.body() == null) {
                    val err = downloadRecord.errorBody()?.string()?.let { JSONObject(it) }
                    downloadLoading.postValue(SingleLiveEvent(false))
                    val errorMsg = err?.getString("error")
                    if (errorMsg != null) {
                        downloadError.postValue(SingleLiveEvent(Pair(true, errorMsg)))
                    }
                } else {
                    downloadLoading.postValue(SingleLiveEvent(false))
                    downloadError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverDownloadCpuRecords.postValue(SingleLiveEvent(downloadRecord.body()))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                downloadLoading.postValue(SingleLiveEvent(false))
                downloadError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

    fun downloadMemRecords(
        interval: String
    ) {
        viewModelScope.launch {
            downloadLoading.postValue(SingleLiveEvent(true))
            downloadError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val downloadRecord = repo.downloadMemoryRecords(interval)
                if (downloadRecord.isSuccessful.not() || downloadRecord.body() == null) {
                    val err = downloadRecord.errorBody()?.string()?.let { JSONObject(it) }
                    downloadLoading.postValue(SingleLiveEvent(false))
                    val errorMsg = err?.getString("error")
                    if (errorMsg != null) {
                        downloadError.postValue(SingleLiveEvent(Pair(true, errorMsg)))
                    }
                } else {
                    downloadLoading.postValue(SingleLiveEvent(false))
                    downloadError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverDownloadMemRecords.postValue(SingleLiveEvent(downloadRecord.body()))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                downloadLoading.postValue(SingleLiveEvent(false))
                downloadError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }

    fun downloadLatRecords(
        interval: String
    ) {
        viewModelScope.launch {
            downloadLoading.postValue(SingleLiveEvent(true))
            downloadError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val downloadRecord = repo.downloadLatencyRecords(interval)
                if (downloadRecord.isSuccessful.not() || downloadRecord.body() == null) {
                    val err = downloadRecord.errorBody()?.string()?.let { JSONObject(it) }
                    downloadLoading.postValue(SingleLiveEvent(false))
                    val errorMsg = err?.getString("error")
                    if (errorMsg != null) {
                        downloadError.postValue(SingleLiveEvent(Pair(true, errorMsg)))
                    }
                } else {
                    downloadLoading.postValue(SingleLiveEvent(false))
                    downloadError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverDownloadLatRecords.postValue(SingleLiveEvent(downloadRecord.body()))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                downloadLoading.postValue(SingleLiveEvent(false))
                downloadError.postValue(SingleLiveEvent(Pair(true, e.message.toString())))
            }
        }
    }
}