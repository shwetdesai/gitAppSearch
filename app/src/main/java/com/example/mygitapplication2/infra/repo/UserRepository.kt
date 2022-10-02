package com.example.mygitapplication2.infra.repo

import android.content.SharedPreferences
import com.example.mygitapplication.infra.model.GitData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class UserRepository @Inject constructor(var sharedPrefs: SharedPreferences) {

    companion object{
        private const val TAG = "UserRepository"
        private const val PREF_USER_DATA = "PREF_USER_DATA"
    }

    fun clearData() {
        sharedPrefs.edit().clear().apply()
    }

    fun setTempUserData(map: HashMap<Int,GitData.UserResults>){
        sharedPrefs.edit().putString(PREF_USER_DATA,Gson().toJson(map)).apply()
    }

    fun getTempUserData(): HashMap<Int, GitData.User>?{
        return Gson().fromJson(
            sharedPrefs.getString(
                PREF_USER_DATA,
                null
            ), object : TypeToken<HashMap<Int, GitData.User>>() {}.type
        )
    }

}