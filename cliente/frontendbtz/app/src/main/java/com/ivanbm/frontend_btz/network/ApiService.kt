package com.ivanbm.frontend_btz.network

import com.ivanbm.frontend_btz.model.LoginRequest
import com.ivanbm.frontend_btz.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>
}