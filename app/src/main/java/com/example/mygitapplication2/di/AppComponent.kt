package com.example.mygitapplication.di

import android.app.Application
import com.example.mygitapplication2.MyGitApplication
import com.example.mygitapplication2.di.ViewModelModule
import com.example.mygitapplication2.view.MainFragment
import com.example.mygitapplication2.view.MainActivity
import com.example.mygitapplication2.view.DetailsFragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ViewModelModule::class])
interface AppComponent {

    // Inject Application
    fun inject(application: MyGitApplication?)

    //Inject Activity
    fun inject(activity: MainActivity)

    //Inject Main Fragment
    fun inject(fragment: MainFragment)

    //Inject Second Fragment
    fun inject(fragment: DetailsFragment)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}