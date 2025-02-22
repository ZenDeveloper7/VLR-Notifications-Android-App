package com.zen.vlrnotifications.network

import com.zen.vlrnotifications.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private val mHttpLogger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private fun instance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(mHttpLogger)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getApi(): Api {
        return instance().create(Api::class.java)
    }
}