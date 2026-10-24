
package com.ivanbm.frontend_btz.network

import android.content.Context

object SessionManager {

    private const val NOMBRE_PREFERENCIAS = "sesion_usuario"
    private const val CLAVE_TOKEN = "token"
    private const val CLAVE_USUARIO_ID = "usuario_id"
    private const val CLAVE_ROL = "rol"

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

    fun guardarUsuario(id: Int, rol: String) {
        preferencias.edit()
            .putInt(CLAVE_USUARIO_ID, id)
            .putString(CLAVE_ROL, rol)
            .apply()
    }

    fun obtenerUsuarioId(): Int {
        return preferencias.getInt(CLAVE_USUARIO_ID, -1)
    }

    fun obtenerRol(): String? {
        return preferencias.getString(CLAVE_ROL, null)
    }

    fun cerrarSesion() {
        preferencias.edit()
            .remove(CLAVE_TOKEN)
            .remove(CLAVE_USUARIO_ID)
            .remove(CLAVE_ROL)
            .apply()
    }
}
