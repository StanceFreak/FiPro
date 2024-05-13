package com.stancefreak.monkob.views.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stancefreak.monkob.remote.model.request.StatusChangeRequest
import com.stancefreak.monkob.remote.model.response.ServerStatusChange
import com.stancefreak.monkob.remote.model.response.ServerUptime
import com.stancefreak.monkob.remote.repository.AppRepository
import com.stancefreak.monkob.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: AppRepository
): ViewModel() {
    private var fetchStatus = true
    private var fetchCount = 0
    private val serverUptime = MutableLiveData<SingleLiveEvent<ServerUptime?>>()
    private val serverChangeStatus = MutableLiveData<SingleLiveEvent<ServerStatusChange?>>()
    private val apiLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val apiError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    fun observeServerUptime(): LiveData<SingleLiveEvent<ServerUptime?>> = serverUptime
    fun observeServerChangeStatus(): LiveData<SingleLiveEvent<ServerStatusChange?>> = serverChangeStatus
    fun observeApiLoading(): LiveData<SingleLiveEvent<Boolean>> = apiLoading
    fun observeApiError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = apiError

    fun getServerUptime() {
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
            val response = repo.getServerUptime()
            fetchStatus = if (response.isSuccessful) {
                apiLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(false, null)))
                serverUptime.postValue(SingleLiveEvent(response.body()?.data))
                true
            } else {
                apiLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, "Terjadi kesalahan")))
                false
        //                    apiError.postValue(SingleLiveEvent(Pair(true, response.errorBody().toString())))
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            apiLoading.postValue(SingleLiveEvent(false))
            apiError.postValue(SingleLiveEvent(Pair(true, "Terjadi kesalahan")))
            fetchStatus = false
        }
    }

    fun postServerChangeStatus(
        command: String
    ) {
        viewModelScope.launch {
            apiLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val status = StatusChangeRequest(
                    command
                )
                val response = repo.postServerStatus(status)
                if (response.isSuccessful) {
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    serverChangeStatus.postValue(SingleLiveEvent(response.body()?.data))
                }
                else {
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(true, "Terjadi kesalahan")))
//                    apiError.postValue(SingleLiveEvent(Pair(true, response.errorBody().toString())))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                apiLoading.postValue(SingleLiveEvent(false))
                apiError.postValue(SingleLiveEvent(Pair(true, "Terjadi kesalahan")))
            }
        }
    }

}