package com.example.mygitapplication2.viewModel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mygitapplication.infra.api.ApiService
import com.example.mygitapplication.infra.model.GitData
import com.example.mygitapplication.infra.network.ConnectionDetector
import com.example.mygitapplication.infra.repo.GitAppRepository
import com.example.mygitapplication.infra.repo.ResultWrapper
import com.example.mygitapplication2.infra.network.ApiStatus
import com.example.mygitapplication2.infra.repo.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class DetailsFragmentViewModel @javax.inject.Inject constructor(
    application: Application, val apiService: ApiService, val userRepository: UserRepository
) : AndroidViewModel(application) {

    var data = MutableLiveData<ArrayList<GitData.User>>()
    var noInternet = MutableLiveData<Boolean>()
    var dataModel = DataModel()
    var apiStatus = MutableLiveData<ApiStatus>()

    companion object{
        private const val TAG = "DetailsFragmentViewMode"
    }


    class DataModel {
        var followersCount = ObservableField("0")
        var noResultFoundVisible = ObservableField(false)
    }

    private fun checkNetwork(): Boolean {
        var isValid = true
        if (!ConnectionDetector.isConnectingToInternet(getApplication())) {
            isValid = false
        }
        return isValid
    }

    fun getAllFollowers(url: String) {
        if (checkNetwork()) {
            viewModelScope.launch {
                apiStatus.postValue(ApiStatus(ApiStatus.SHOW_PROGRESS, false))
                GitAppRepository.getFollowersInfo(apiService, url).collect {
                    when (it) {
                        is ResultWrapper.Success -> onSuccessUsersData(it.value)
                        is ResultWrapper.Error -> onFailureUsersData(it.throwable)
                    }
                }
            }
        } else {
            Log.i(TAG, "No Internet")
            noInternet.postValue(true)
        }
    }

    private fun onSuccessUsersData(result: ArrayList<GitData.User>) {
        Log.i(TAG, "onSuccessReposData ${Gson().toJson(result)}")
        apiStatus.postValue(ApiStatus(ApiStatus.HIDE_PROGRESS, false))
        if (result.isNotEmpty()) {
            dataModel.noResultFoundVisible.set(false)
            data.postValue(result)
        }else {
            dataModel.noResultFoundVisible.set(true)
        }
    }

    private fun onFailureUsersData(throwable: Throwable?) {
        apiStatus.postValue(ApiStatus(ApiStatus.HIDE_PROGRESS, false))
        Log.i(TAG, "onFailureReposData $throwable")
    }
}