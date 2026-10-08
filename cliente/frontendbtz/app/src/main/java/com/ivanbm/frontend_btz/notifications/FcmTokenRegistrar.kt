package com.ivanbm.frontend_btz.notifications

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.ivanbm.frontend_btz.model.FcmTokenRequest
import com.ivanbm.frontend_btz.network.RetrofitClient
import com.ivanbm.frontend_btz.network.SessionManager

object FcmTokenRegistrar {

    fun registrar(context: Context) {
        SessionManager.inicializar(context.applicationContext)
        if (SessionManager.obtenerToken().isNullOrEmpty()) return

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { fcmToken ->
                RetrofitClient.api.guardarTokenFcm(FcmTokenRequest(fcmToken)).enqueue(
                    object : retrofit2.Callback<Void> {
                        override fun onResponse(
                            call: retrofit2.Call<Void>,
                            response: retrofit2.Response<Void>
                        ) {
                            if (!response.isSuccessful) {
                                Log.e(TAG, "No se pudo registrar el token FCM: HTTP ${response.code()}")
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                            Log.e(TAG, "Error al registrar el token FCM", t)
                        }
                    }
                )
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "No se pudo obtener el token FCM", exception)
            }
    }

    private const val TAG = "FcmTokenRegistrar"
    }
}