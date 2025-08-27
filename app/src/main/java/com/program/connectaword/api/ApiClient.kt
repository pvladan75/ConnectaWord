package com.program.connectaword.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var apiService: ApiService? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    fun initialize(ipAddress: String) {
        ServerConfig.setIpAddress(ipAddress)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ipAddress:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    fun getApiService(): ApiService {
        if (apiService == null) {
            throw IllegalStateException("ApiClient must be initialized!")
        }
        return apiService!!
    }
}