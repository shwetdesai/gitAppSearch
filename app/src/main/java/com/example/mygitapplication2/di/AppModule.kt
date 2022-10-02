package com.example.mygitapplication.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

import com.example.mygitapplication.infra.api.ApiService
import com.example.mygitapplication.infra.model.Constants.Companion.CUSTOM_CONNECT_TIMEOUT
import com.example.mygitapplication.infra.model.Constants.Companion.CUSTOM_READ_TIMEOUT
import com.example.mygitapplication.infra.model.Constants.Companion.GIT_BASE_URL
import com.example.mygitapplication2.BuildConfig
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton



@Module
class AppModule {

    companion object{
        private const val TAG = "NetworkModule"

        const val CONNECT_TIMEOUT = 60 // seconds

        const val READ_TIMEOUT = 60 // seconds

        const val WRITE_TIMEOUT = 60 // seconds

    }

    @Provides
    @Singleton
    fun provideContext(application: Application?): Context? {
        return application
    }

    @Provides
    @Singleton
    fun provideGson(): Gson? {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    @Provides
    fun provideDefaultSharedPreferences(context: Context?): SharedPreferences {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideHttpCache(application: Application): Cache? {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(cache: Cache?): OkHttpClient? {
        val client: OkHttpClient.Builder = OkHttpClient.Builder()
            .connectTimeout(CUSTOM_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(CUSTOM_READ_TIMEOUT, TimeUnit.SECONDS)
        client.cache(cache)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            client.addInterceptor(logging)
        }
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson?, okHttpClient: OkHttpClient?): Retrofit? {
        return Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create()
                )
            ) //.baseUrl(BASE_URL)
            .baseUrl(GIT_BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiServiceV2(): ApiService {
        val client: OkHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(getHttpLoggingInterceptor())
            .connectTimeout(
                CONNECT_TIMEOUT.toLong(),
                TimeUnit.SECONDS
            )
            .readTimeout(
                READ_TIMEOUT.toLong(),
                TimeUnit.SECONDS
            )
            .writeTimeout(
                WRITE_TIMEOUT.toLong(),
                TimeUnit.SECONDS
            )
            .cache(null)
            .build()
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
        val retrofit: Retrofit = Retrofit.Builder()
            .client(client) //.baseUrl(BuildConfig.BASE_URL)
            .baseUrl(GIT_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(ApiService::class.java)
    }

    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return loggingInterceptor
    }
}