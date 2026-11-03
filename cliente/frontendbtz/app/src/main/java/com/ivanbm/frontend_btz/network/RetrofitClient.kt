package com.ivanbm.frontend_btz.network

import android.content.Intent
import com.google.gson.GsonBuilder
import com.ivanbm.frontend_btz.BtzApplication
import com.ivanbm.frontend_btz.BuildConfig
import com.ivanbm.frontend_btz.LoginActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor(Interceptor { cadena ->
            val original = cadena.request()
            val token = SessionManager.obtenerToken()

            val solicitud = original.newBuilder()
                .header("Accept", "application/json")
                .apply {
                    if (!token.isNullOrEmpty()) header("Authorization", "Bearer $token")
                }
                .build()

            val respuesta = cadena.proceed(solicitud)

            // Token vencido/revocado: limpiar sesión y volver al login (excepto en el propio login).
            if (respuesta.code == 401 && !solicitud.url.encodedPath.endsWith("/login")) {
                SessionManager.cerrarSesion()
                val app = BtzApplication.instance
                app.startActivity(
                    Intent(app, LoginActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
            }
            respuesta
        })
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(Boolean::class.java, BooleanAdapter())
        .create()

    val api: ApiService by lazy {
        Retrofit.Builder()
            // En build.gradle.kts -> lineas 25 y 28 estan las URLs (25 la de test, 28 la de prod)
            .baseUrl(BuildConfig.BASE_URL)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}