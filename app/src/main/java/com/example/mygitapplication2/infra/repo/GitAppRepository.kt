package com.example.mygitapplication.infra.repo

import com.example.mygitapplication.infra.api.ApiService
import com.example.mygitapplication.infra.model.GitData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


object GitAppRepository {
    suspend fun getUserInfo(apiService: ApiService,searchString: String,page: Int) : Flow<ResultWrapper<GitData.UserResults>> {
        return flow{
            when (val data = safeApiCall { apiService.getGitUser(searchString,page) }) {
                is ResultWrapper.Error -> emit(
                    ResultWrapper.Error(
                        data.throwable,
                        data.errorMessage
                    )
                )
                is ResultWrapper.Success -> emit(
                    ResultWrapper.Success(
                        data.value
                    )
                )
            }
        }
    }

    suspend fun getFollowersInfo(apiService: ApiService,url: String) : Flow<ResultWrapper<ArrayList<GitData.User>>> {
        return flow{
            when (val data = safeApiCall { apiService.getFollowersInfo(url) }) {
                is ResultWrapper.Error -> emit(
                    ResultWrapper.Error(
                        data.throwable,
                        data.errorMessage
                    )
                )
                is ResultWrapper.Success -> emit(
                    ResultWrapper.Success(
                        data.value
                    )
                )
            }
        }
    }
}