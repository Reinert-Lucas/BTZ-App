package com.ivanbm.frontend_btz.model

data class UsuarioRequest(
    val nombre: String,
    val password: String?,
    val rol: String,
    val dni: String,
    val telefono: String,
)