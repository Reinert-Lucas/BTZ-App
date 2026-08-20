package com.ivanbm.frontend_btz.model

data class Cliente(
    val id: Int,
    val nombre: String,
    val email: String,
    val asegurado: Boolean,
    val asegurado_detalle: String?
)