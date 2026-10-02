package com.ivanbm.frontend_btz.model

data class MaterialesResponse(
    val status: Boolean,
    val message: String,
    val data: List<Material>,
    val meta: MetaAvisos
)