package com.ivanbm.frontend_btz.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor(Interceptor { cadena ->

            val token = SessionManager.obtenerToken()

            val solicitudOriginal = cadena.request()

            val solicitudNueva = if (!token.isNullOrEmpty()) {

                solicitudOriginal.newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer $token"
                    )
                    .build()

            } else {
                solicitudOriginal
            }

            cadena.proceed(solicitudNueva)
        })
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(
            Boolean::class.java,
            BooleanAdapter()
        )
        .create()

    val api: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clienteHttp)
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .build()
            .create(ApiService::class.java)
    }
}