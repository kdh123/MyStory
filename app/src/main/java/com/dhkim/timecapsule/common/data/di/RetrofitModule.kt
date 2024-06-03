package com.dhkim.timecapsule.common.data.di

import com.dhkim.timecapsule.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val SERVER_URL = "https://dapi.kakao.com/v2/local/"

    @Provides
    @Singleton
    fun serverBuilder(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(client)
            .addConverterFactory (gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun client() : OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
        .build()

    @Provides
    @Singleton
    fun gsonConverterFactory(gson : Gson) : GsonConverterFactory = GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun gson() : Gson = GsonBuilder()
        .setLenient()
        .create()
}