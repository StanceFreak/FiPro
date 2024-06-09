package com.stancefreak.monkob.views.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stancefreak.monkob.remote.model.request.RegisterDeviceRequest
import com.stancefreak.monkob.remote.model.response.RegisterDevice
import com.stancefreak.monkob.remote.repository.AppRepository
import com.stancefreak.monkob.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SplashViewModel@Inject constructor(
    private val repo: AppRepository
): ViewModel() {
    private var deviceRegister = MutableLiveData<SingleLiveEvent<RegisterDevice?>>()
    private val deviceRegisterLoading = MutableLiveData<SingleLiveEvent<Boolean>>()
    private val deviceRegisterError = MutableLiveData<SingleLiveEvent<Pair<Boolean, String?>>>()

    fun observeDeviceRegister(): LiveData<SingleLiveEvent<RegisterDevice?>> = deviceRegister
    fun observeDeviceRegisterLoading(): LiveData<SingleLiveEvent<Boolean>> = deviceRegisterLoading
    fun observeDeviceRegisterError(): LiveData<SingleLiveEvent<Pair<Boolean, String?>>> = deviceRegisterError

    fun registerDevice(
        token: String
    ) {
        viewModelScope.launch {
            deviceRegisterLoading.postValue(SingleLiveEvent(true))
            deviceRegisterError.postValue(SingleLiveEvent(Pair(false, null)))
            try {
                val deviceToken = RegisterDeviceRequest(
                    token
                )
                val response = repo.postRegisterDevice(deviceToken)
                if (response.isSuccessful) {
                    deviceRegisterLoading.postValue(SingleLiveEvent(false))
                    deviceRegisterError.postValue(SingleLiveEvent(Pair(false, null)))
                    deviceRegister.postValue(SingleLiveEvent(response.body()))
                }
                else {
                    val err = response.errorBody()?.string()?.let { JSONObject(it) }
                    val errorMsg = err?.getString("error")
                    if (errorMsg != null) {
                        deviceRegisterError.postValue(SingleLiveEvent(Pair(true, errorMsg)))
                    }
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                deviceRegisterLoading.postValue(SingleLiveEvent(false))
                deviceRegisterError.postValue(SingleLiveEvent(Pair(true, e.message)))
            }
        }
    }
}