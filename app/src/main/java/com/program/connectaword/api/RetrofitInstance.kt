package com.program.connectaword.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // Adresa našeg budućeg servera.
    // 10.0.2.2 je specijalna adresa koja sa Android emulatora gađa localhost na tvom računaru.
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Kreiramo interceptor za logovanje kako bismo u Logcat-u videli detalje mrežnih poziva.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Kreiramo OkHttp klijent sa našim interceptor-om.
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Lazy inicijalizacija Retrofit instance.
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}