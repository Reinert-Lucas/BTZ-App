package com.ivanbm.frontend_btz.model

data class AvisoRequest(
    val fecha: String,
    val hora: String,
    val direccion: String,
    val telefono: String,
    val mensaje: String?,
    val observacion: String?,
    val estado: String,
    val urgencia: String,
    val usuario_id: Int,
    val cliente_id: Int
)