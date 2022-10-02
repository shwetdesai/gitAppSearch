package com.example.mygitapplication.infra.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GitData {

    data class UserResults(
        @SerializedName("total_count") @Expose var totalCount: Int? = null,
        @SerializedName("incomplete_results") @Expose var incompleteResults: Boolean? = null,
        @SerializedName("items") @Expose var items: ArrayList<User>? = null
    )

    data class User(
        @SerializedName("login") @Expose var userName: String? = null,
        @SerializedName("avatar_url") @Expose var userImage: String? = null,
        @SerializedName("followers_url") @Expose var followersUrl: String? = null,
        @SerializedName("following_url") @Expose var followingUrl: String? = null,
        @SerializedName("id") @Expose var id: String? = null,
        @SerializedName("type") @Expose var type: String? = null
    )
}