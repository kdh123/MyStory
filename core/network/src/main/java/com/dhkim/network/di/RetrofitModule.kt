package com.dhkim.network.di

import com.dhkim.network.BuildConfig
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
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val KAKAO_LOCAL_URL = "https://dapi.kakao.com/v2/local/"
    private const val KAKAO_PUSH_URL = "https://kapi.kakao.com/v2/push/"
    private const val FCM_URL = BuildConfig.FCM_URL

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoLocal

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoPush

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Fcm

    @KakaoPush
    @Provides
    @Singleton
    fun kakaoPushServerBuilder(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(KAKAO_PUSH_URL)
            .client(client)
            .addConverterFactory (gsonConverterFactory)
            .build()
    }

    @KakaoLocal
    @Provides
    @Singleton
    fun kakaoLocalServerBuilder(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(KAKAO_LOCAL_URL)
            .client(client)
            .addConverterFactory (gsonConverterFactory)
            .build()
    }

    @Fcm
    @Provides
    @Singleton
    fun fcmBuilder(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FCM_URL)
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