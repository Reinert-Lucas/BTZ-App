package com.ivanbm.frontend_btz.network

import android.content.Context

object SessionManager {

    private const val NOMBRE_PREFERENCIAS = "sesion_usuario"
    private const val CLAVE_TOKEN = "token"

    private lateinit var preferencias: android.content.SharedPreferences

    fun inicializar(context: Context) {
        preferencias = context.getSharedPreferences(
            NOMBRE_PREFERENCIAS,
            Context.MODE_PRIVATE
        )
    }

    fun guardarToken(token: String) {
        preferencias.edit()
            .putString(CLAVE_TOKEN, token)
            .apply()
    }

    fun obtenerToken(): String? {
        return preferencias.getString(CLAVE_TOKEN, null)
    }

    fun cerrarSesion() {
        preferencias.edit()
            .remove(CLAVE_TOKEN)
            .apply()
    }
}