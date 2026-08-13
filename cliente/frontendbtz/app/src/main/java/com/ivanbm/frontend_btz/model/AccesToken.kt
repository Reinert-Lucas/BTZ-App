package com.ivanbm.frontend_btz.model

data class AccessToken(
    val name: String?,
    val abilities: List<String>?,
    val expires_at: String?,
    val tokenable_id: Int?,
    val tokenable_type: String?,
    val updated_at: String?,
    val created_at: String?,
    val id: Int?
)