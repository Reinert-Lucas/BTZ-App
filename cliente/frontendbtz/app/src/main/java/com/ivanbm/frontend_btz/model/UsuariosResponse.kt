package com.ivanbm.frontend_btz.model

data class UsuariosResponse(
    val status: Boolean,
    val message: String,
    val data: List<Usuario>,
    val links: PaginacionLinks?,
    val meta: PaginacionMeta?
)