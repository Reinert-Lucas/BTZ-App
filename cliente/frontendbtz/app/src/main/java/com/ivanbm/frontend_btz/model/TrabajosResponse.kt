package com.ivanbm.frontend_btz.model

data class TrabajosResponse(
    val status: Boolean,
    val message: String,
    val data: List<Aviso>,
    val meta: MetaAvisos
)