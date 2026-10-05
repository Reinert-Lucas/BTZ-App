package com.ivanbm.frontend_btz.model

data class AvisosResponse(
    val status: Boolean,
    val message: String,
    val data: List<Aviso>,
    val meta: MetaAvisos
)

data class MetaAvisos(
    val current_page: Int,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)