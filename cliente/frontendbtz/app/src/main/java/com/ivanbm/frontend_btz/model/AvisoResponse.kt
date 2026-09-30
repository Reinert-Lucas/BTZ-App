package com.ivanbm.frontend_btz.model

data class AvisoResponse(
    val status: Boolean,
    val message: String,
    val data: Aviso
)