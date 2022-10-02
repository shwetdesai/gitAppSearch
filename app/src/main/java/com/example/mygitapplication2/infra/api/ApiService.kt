package com.example.mygitapplication.infra.api

import com.example.mygitapplication.infra.model.Constants.Companion.GIT_PULL_URL
import com.example.mygitapplication.infra.model.GitData
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET(GIT_PULL_URL)
    suspend fun getGitUser(
        @Query("q") searchString:String,
        @Query("page") page:Int
    ): GitData.UserResults

    @GET
    suspend fun getFollowersInfo(
        @Url url:String
    ): ArrayList<GitData.User>
}