package com.ivanbm.frontend_btz

import android.app.Activity
import android.content.Intent
import com.ivanbm.frontend_btz.network.RetrofitClient
import com.ivanbm.frontend_btz.network.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CerrarSesion {

    fun ejecutar(activity: Activity) {
        RetrofitClient.api.cerrarSesion().enqueue(object : Callback<Void> {

            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                finalizarSesion(activity)
            }

            override fun onFailure(
                call: Call<Void>,
                t: Throwable
            ) {
                // Si falla la conexión, igualmente se cierra
                finalizarSesion(activity)
            }
        })
    }

    private fun finalizarSesion(activity: Activity) {
        SessionManager.cerrarSesion()

        val intent = Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        activity.startActivity(intent)
        activity.finish()
    }
}
