package com.stancefreak.monkob.views.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stancefreak.monkob.remote.model.request.StatusChangeRequest
import com.stancefreak.monkob.remote.model.response.ServerStatus
import com.stancefreak.monkob.remote.model.response.ServerStatusChange
import com.stancefreak.monkob.remote.model.response.ServerUptime
import com.stancefreak.monkob.remote.repository.AppRepository
import com.stancefreak.monkob.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: AppRepository
): ViewModel() {
    private var fetchStatus = true
    private var fetchCount = 0
    private val serverUptime = MutableLiveData<SingleLiveEvent<ServerUptime?>>()
    private val serverStatus = MutableLiveData<SingleLiveEvent<ServerStatus?>>()
    private val serverChangeStatus = MutableLiveData<SingleLiveEvent<ServerStatusChange?>>()
    private val serverUptimeLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val serverStatusLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val serverChangeStatusLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val serverUptimeError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    private val serverStatusError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    private val serverChangeStatusError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    fun observeServerUptime(): LiveData<SingleLiveEvent<ServerUptime?>> = serverUptime
    fun observeServerStatus(): LiveData<SingleLiveEvent<ServerStatus?>> = serverStatus
    fun observeServerChangeStatus(): LiveData<SingleLiveEvent<ServerStatusChange?>> = serverChangeStatus
    fun observeServerUptimeLoading(): LiveData<SingleLiveEvent<Boolean>> = serverUptimeLoading
    fun observeServerStatusLoading(): LiveData<SingleLiveEvent<Boolean>> = serverStatusLoading
    fun observeServerChangeStatusLoading(): LiveData<SingleLiveEvent<Boolean>> = serverChangeStatusLoading
    fun observeServerUptimeError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = serverUptimeError
    fun observeServerStatusError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = serverStatusError
    fun observeServerChangeStatusError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = serverChangeStatusError

    fun fetchServerUptime(loading: Boolean) {
        viewModelScope.launch {
            serverUptimeLoading.postValue(SingleLiveEvent(loading))
            serverUptimeError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val startTime = System.currentTimeMillis()
                val response = repo.getServerUptime()
                if (response.isSuccessful) {
                    serverUptimeLoading.postValue(SingleLiveEvent(false))
                    serverUptimeError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverUptime.postValue(SingleLiveEvent(response.body()?.data))
                    val elapsedTime = System.currentTimeMillis() - startTime
                    Log.d("uptime delay logging: ", elapsedTime.toString())
                } else {
                    serverUptimeLoading.postValue(SingleLiveEvent(false))
                    val err = response.errorBody()?.string()?.let { JSONObject(it) }
                    val errorMsg = err?.getString("error")
                    if (errorMsg != null) {
                        Log.d("tes error", errorMsg)
                        serverUptimeError.postValue(SingleLiveEvent(Pair(true, errorMsg)))
                    }
                    serverUptime.postValue(SingleLiveEvent(null))
                    val elapsedTime = System.currentTimeMillis() - startTime
                    Log.d("uptime delay logging: ", elapsedTime.toString())
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                serverUptimeLoading.postValue(SingleLiveEvent(false))
                serverUptimeError.postValue(SingleLiveEvent(Pair(true, "Terjadi kesalahan")))
            }
        }
    }

    fun fetchServerStatus(loading: Boolean) {
        viewModelScope.launch {
            serverStatusLoading.postValue(SingleLiveEvent(loading))
            serverStatusError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
//                val startTime = System.currentTimeMillis()
                val response = repo.getServerStatus()
                if (response.isSuccessful) {
                    serverStatusLoading.postValue(SingleLiveEvent(false))
                    serverStatusError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverStatus.postValue(SingleLiveEvent(response.body()?.data))
//                    val elapsedTime = System.currentTimeMillis() - startTime
//                    Log.d("status delay logging: ", elapsedTime.toString())
                } else {
                    serverStatusLoading.postValue(SingleLiveEvent(false))
                    val err = response.errorBody()?.string()?.let { JSONObject(it) }
                    val errorMsg = err?.getString("error")
                    if (errorMsg != null) {
                        serverStatusError.postValue(SingleLiveEvent(Pair(true, errorMsg)))
                    }
                    serverStatus.postValue(SingleLiveEvent(null))
//                    val elapsedTime = System.currentTimeMillis() - startTime
//                    Log.d("status delay logging: ", elapsedTime.toString())
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                serverStatusLoading.postValue(SingleLiveEvent(false))
                serverStatusError.postValue(SingleLiveEvent(Pair(true, "Terjadi kesalahan")))
            }
        }
    }

    fun postServerChangeStatus(
        command: String
    ) {
        viewModelScope.launch {
            serverChangeStatusLoading.postValue(SingleLiveEvent(true))
            serverChangeStatusError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val status = StatusChangeRequest(
                    command
                )
                val response = repo.postServerStatus(status)
                if (response.isSuccessful) {
                    serverChangeStatusLoading.postValue(SingleLiveEvent(false))
                    serverChangeStatusError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverChangeStatus.postValue(SingleLiveEvent(response.body()?.data))
                }
                else {
                    serverChangeStatusLoading.postValue(SingleLiveEvent(false))
                    serverChangeStatusError.postValue(SingleLiveEvent(Pair(true, "Terjadi kesalahan")))
//                    apiError.postValue(SingleLiveEvent(Pair(true, response.errorBody().toString())))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                serverChangeStatusLoading.postValue(SingleLiveEvent(false))
                serverChangeStatusError.postValue(SingleLiveEvent(Pair(true, "Terjadi kesalahan")))
            }
        }
    }

}