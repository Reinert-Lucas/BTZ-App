package com.ivanbm.frontend_btz.model

data class ClienteRequest(
    val nombre: String,
    val email: String,
    val asegurado: Boolean,
    val asegurado_detalle: String?
)