package com.ivanbm.frontend_btz.model

data class ClienteAviso(
    val id: Int,
    val nombre: String,
    val asegurado: Boolean,
    val asegurado_detalle: String?
)
