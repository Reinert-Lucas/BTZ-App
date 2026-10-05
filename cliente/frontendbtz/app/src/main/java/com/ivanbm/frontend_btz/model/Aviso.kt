package com.ivanbm.frontend_btz.model

data class Aviso(
    val id: Int,
    val fecha: String,
    val hora: String,
    val direccion: String,
    val telefono: String,
    val mensaje: String?,
    val observacion: String?,
    val estado: String,
    val urgencia: String,
    val operario: Operario,
    val cliente: ClienteAviso
)