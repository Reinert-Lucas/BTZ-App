package com.ivanbm.frontend_btz.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val rol: String,
    val dni: String,
    val telefono: String,
    val password: String? = null,
)