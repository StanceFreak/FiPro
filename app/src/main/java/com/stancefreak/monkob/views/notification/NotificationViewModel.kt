package com.stancefreak.monkob.views.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stancefreak.monkob.remote.model.response.NotificationParentResponse
import com.stancefreak.monkob.remote.repository.AppRepository
import com.stancefreak.monkob.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repo: AppRepository
): ViewModel() {
    private val serverNotif = MutableLiveData<SingleLiveEvent<ArrayList<NotificationParentResponse>?>>()
    private val apiLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val apiError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()
    private val notifParentList = ArrayList<NotificationParentResponse>()
    fun observeServerNotif(): LiveData<SingleLiveEvent<ArrayList<NotificationParentResponse>?>> = serverNotif
    fun observeApiLoading(): LiveData<SingleLiveEvent<Boolean>> = apiLoading
    fun observeApiError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = apiError

//    fun getServerUptime() {
//        val firstCallTime = ceil(System.currentTimeMillis() / 60_000.0).toLong() * 60_000
//        viewModelScope.launch {
//            fetchData(true)
//            delay(firstCallTime - System.currentTimeMillis())
//            while (fetchStatus) {
//                launch {
//                    if (fetchCount > 0) {
//                        fetchData(false)
//                    }
//                }
//                delay(60_000)
//            }
//        }
//    }

    fun fetchNotifRecords() {
        viewModelScope.launch {
            apiLoading.postValue(SingleLiveEvent(true))
            apiError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                var id = 0
                val response = repo.getServerNotifRecord()
                if (response.isSuccessful) {
                    notifParentList.clear()
                    apiLoading.postValue(SingleLiveEvent(false))
                    apiError.postValue(SingleLiveEvent(Pair(false, null)))
                    val notifList = response.body()?.data?.groupBy {
                        it.date
                    }
                    if (notifList != null) {
                        for (item in notifList) {
                            notifParentList.add(NotificationParentResponse(id++, item.key, item.value))
                        }
                        serverNotif.postValue((SingleLiveEvent(notifParentList)))
                    }
                } else {
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