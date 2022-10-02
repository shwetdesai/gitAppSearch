package com.example.mygitapplication2

import android.app.Application
import com.example.mygitapplication.di.AppComponent
import com.example.mygitapplication.di.DaggerAppComponent


class MyGitApplication: Application() {

    var appComponents: AppComponent? = null
    companion object{

        private var instance: MyGitApplication? = null

        private const val TAG = "MyGitApplication"

        fun getInstance(): MyGitApplication? {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupDependencyInjection()
        getInstance()?.getAppComponent()?.inject(this)
    }


    fun getAppComponent(): AppComponent? {
        return appComponents
    }

    private fun setupDependencyInjection() {
        appComponents = DaggerAppComponent
            .builder()
            .application(this)
            .build()
    }
}
