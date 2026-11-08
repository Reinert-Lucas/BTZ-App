package com.ivanbm.frontend_btz.notifications

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.ivanbm.frontend_btz.model.FcmTokenRequest
import com.ivanbm.frontend_btz.network.RetrofitClient
import com.ivanbm.frontend_btz.network.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FcmTokenRegistrar {

    fun registrar(context: Context) {
        SessionManager.inicializar(context.applicationContext)

        if (SessionManager.obtenerToken().isNullOrEmpty()) {
            Log.w(TAG, "No se registra el token FCM: no hay sesión de API.")
            return
        }

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { fcmToken ->
                Log.i(TAG, "Token FCM obtenido; enviando registro a la API.")
                RetrofitClient.api
                    .guardarTokenFcm(FcmTokenRequest(fcmToken))
                    .enqueue(object : Callback<Void> {

                        override fun onResponse(
                            call: Call<Void>,
                            response: Response<Void>
                        ) {
                            val endpoint = call.request().url.toString()
                            if (!response.isSuccessful) {
                                val errorBody = response.errorBody()?.string()
                                Log.e(
                                    TAG,
                                    "No se pudo registrar el token FCM: HTTP ${response.code()}, URL=$endpoint, respuesta=$errorBody"
                                )
                            } else {
                                Log.i(TAG, "Token FCM registrado en la API: HTTP ${response.code()}, URL=$endpoint.")
                            }
                        }

                        override fun onFailure(
                            call: Call<Void>,
                            t: Throwable
                        ) {
                            Log.e(
                                TAG,
                                "Error al registrar el token FCM en ${call.request().url}",
                                t
                            )
                        }
                    })
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "No se pudo obtener el token FCM", exception)
            }
    }

    private const val TAG = "FcmTokenRegistrar"
}