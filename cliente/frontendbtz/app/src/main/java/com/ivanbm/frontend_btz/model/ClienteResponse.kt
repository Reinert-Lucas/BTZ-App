package com.ivanbm.frontend_btz.model

data class ClienteResponse(
    val status: Boolean,
    val message: String,
    val data: Cliente
)