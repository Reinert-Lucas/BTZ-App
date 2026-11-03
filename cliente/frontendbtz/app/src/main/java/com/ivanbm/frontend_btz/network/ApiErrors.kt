package com.ivanbm.frontend_btz.network

import org.json.JSONObject

object ApiErrors {
    /** Devuelve el `message` del JSON de error del backend, o un texto por defecto. */
    fun mensaje(cuerpo: String?, porDefecto: String): String = try {
        JSONObject(cuerpo ?: "").optString("message").ifBlank { porDefecto }
    } catch (e: Exception) {
        porDefecto
    }
}

// Uso:
// Toast.makeText(this@EditarUsuarioActivity,
//     ApiErrors.mensaje(response.errorBody()?.string(), "No se pudo completar la operación"),
//     Toast.LENGTH_LONG).show()