package com.ivanbm.frontend_btz.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SessionManager {

    private const val TAG = "SessionManager"
    private const val NOMBRE_PREFERENCIAS_LEGACY = "sesion_usuario"
    private const val NOMBRE_PREFERENCIAS_SEGURAS = "sesion_usuario_segura"
    private const val CLAVE_TOKEN = "token"
    private const val CLAVE_USUARIO_ID = "usuario_id"
    private const val CLAVE_ROL = "rol"
    private const val CLAVE_MIGRACION_COMPLETA = "migracion_completa"

    private lateinit var preferencias: SharedPreferences
    private lateinit var appContext: Context

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

    fun inicializar(context: Context) {
        appContext = context.applicationContext
        preferencias = crearPreferenciasSeguras(appContext)
        migrarPreferenciasLegacySiCorresponde(appContext)
    }

    fun expirarSesion() {
        cerrarSesion()
        val intent = android.content.Intent(appContext, com.ivanbm.frontend_btz.LoginActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        appContext.startActivity(intent)
    }

    private fun crearPreferenciasSeguras(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                NOMBRE_PREFERENCIAS_SEGURAS,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo inicializar el almacenamiento cifrado de sesión", e)
            context.getSharedPreferences(NOMBRE_PREFERENCIAS_LEGACY, Context.MODE_PRIVATE)
        }
    }

    private fun migrarPreferenciasLegacySiCorresponde(context: Context) {
        if (preferencias.getBoolean(CLAVE_MIGRACION_COMPLETA, false)) {
            return
        }

        val legacy = context.getSharedPreferences(NOMBRE_PREFERENCIAS_LEGACY, Context.MODE_PRIVATE)
        val tokenLegacy = legacy.getString(CLAVE_TOKEN, null)
        val usuarioIdLegacy = legacy.getInt(CLAVE_USUARIO_ID, -1)
        val rolLegacy = legacy.getString(CLAVE_ROL, null)

        val editor = preferencias.edit()
        if (!tokenLegacy.isNullOrBlank() && preferencias.getString(CLAVE_TOKEN, null).isNullOrBlank()) {
            editor.putString(CLAVE_TOKEN, tokenLegacy)
        }
        if (usuarioIdLegacy > 0 && preferencias.getInt(CLAVE_USUARIO_ID, -1) <= 0) {
            editor.putInt(CLAVE_USUARIO_ID, usuarioIdLegacy)
        }
        if (!rolLegacy.isNullOrBlank() && preferencias.getString(CLAVE_ROL, null).isNullOrBlank()) {
            editor.putString(CLAVE_ROL, rolLegacy)
        }
        editor.putBoolean(CLAVE_MIGRACION_COMPLETA, true)
        editor.apply()

        if (legacy.all.isNotEmpty()) {
            legacy.edit().clear().apply()
        }
    }
}
