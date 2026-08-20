package com.ivanbm.frontend_btz.model

data class ClientesResponse(
    val status: Boolean,
    val message: String,
    val data: List<Cliente>,
    val links: PaginacionLinks,
    val meta: PaginacionMeta
)

data class PaginacionLinks(
    val first: String?,
    val last: String?,
    val prev: String?,
    val next: String?
)

data class PaginacionMeta(
    val current_page: Int,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)