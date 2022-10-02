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

class MainFragmentViewModel @javax.inject.Inject constructor(
    application: Application, val apiService: ApiService, val userRepository: UserRepository
) : AndroidViewModel(application) {

    var data = MutableLiveData<ArrayList<GitData.User>>()
    var noInternet = MutableLiveData<Boolean>()
    var dataModel = DataModel()
    var apiStatus = MutableLiveData<ApiStatus>()

    companion object {
        private const val TAG = "MainFragmentViewModel"
    }

    class DataModel{
        var noResultFoundVisible = ObservableField(false)
    }

    private fun checkNetwork(): Boolean {
        var isValid = true
        if (!ConnectionDetector.isConnectingToInternet(getApplication())) {
            isValid = false
        }
        return isValid
    }


    fun getAllGitUsers(searchString: String, page: Int) {
        if (checkNetwork()) {
            viewModelScope.launch {
                apiStatus.postValue(ApiStatus(ApiStatus.SHOW_PROGRESS, false))
                GitAppRepository.getUserInfo(apiService, searchString, page).collect {
                    when (it) {
                        is ResultWrapper.Success -> onSuccessUsersData(it.value,page)
                        is ResultWrapper.Error -> onFailureUsersData(it.throwable)
                    }
                }
            }
        } else {
            Log.i(TAG, "No Internet")
            noInternet.postValue(true)
        }
    }

    private fun onSuccessUsersData(result: GitData.UserResults, page: Int) {
        Log.i(TAG, "onSuccessReposData ${Gson().toJson(result)}")
        apiStatus.postValue(ApiStatus(ApiStatus.HIDE_PROGRESS, false))
        if (!result.items.isNullOrEmpty()) {
            dataModel.noResultFoundVisible.set(false)
            data.postValue(result.items!!)
            var map = HashMap<Int,GitData.UserResults>()
            map[page] = result
            userRepository.setTempUserData(map)
        }else
            dataModel.noResultFoundVisible.set(true)
    }

    private fun onFailureUsersData(throwable: Throwable?) {
        apiStatus.postValue(ApiStatus(ApiStatus.HIDE_PROGRESS, false))
        Log.i(TAG, "onFailureReposData $throwable")
    }
}