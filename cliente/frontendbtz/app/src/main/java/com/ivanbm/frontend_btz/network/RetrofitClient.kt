package com.ivanbm.frontend_btz.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    //private const val BASE_URL = "http://10.167.207.140:8000/api/"
    private const val BASE_URL = "http://10.167.207.140:8000/api/"
    //private const val BASE_URL = "http://10.0.2.2:8000/api/" emulador

    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor(Interceptor { cadena ->
            val token = SessionManager.obtenerToken()
            val original = cadena.request()
            val solicitud = if (!token.isNullOrEmpty()) {
                original.newBuilder().addHeader("Authorization", "Bearer $token").build()
            } else original
        
            val respuesta = cadena.proceed(solicitud)
        
            // 401 con un token guardado (y que no sea el propio login) = sesión vencida o desplazada.
            if (respuesta.code == 401 && !token.isNullOrEmpty() &&
                !original.url.encodedPath.endsWith("/login")
            ) {
                SessionManager.expirarSesion()
            }
            respuesta
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