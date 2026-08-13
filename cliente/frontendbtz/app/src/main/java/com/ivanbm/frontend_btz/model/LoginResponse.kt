package com.ivanbm.frontend_btz.model

class LoginResponse (
    val status: Boolean,
    val message: String,
    val user: Usuario?,
    val token: TokenResponse?
)