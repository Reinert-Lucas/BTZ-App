package com.ivanbm.frontend_btz.network

import com.ivanbm.frontend_btz.model.ClienteRequest
import com.ivanbm.frontend_btz.model.ClienteResponse
import com.ivanbm.frontend_btz.model.ClientesResponse
import com.ivanbm.frontend_btz.model.LoginRequest
import com.ivanbm.frontend_btz.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // LOGIN
    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>


    // CLIENTES

    // Obtener todos los clientes
    @GET("clientes")
    fun obtenerClientes(
        @Query("page") pagina: Int
    ): Call<ClientesResponse>

    // Obtener un cliente
    @GET("clientes/{id}")
    fun obtenerCliente(
        @Path("id") id: Int
    ): Call<ClienteResponse>


    // Crear cliente
    @POST("clientes")
    fun crearCliente(
        @Body request: ClienteRequest
    ): Call<ClienteResponse>


    // Actualizar cliente
    @PUT("clientes/{id}")
    fun actualizarCliente(
        @Path("id") id: Int,
        @Body request: ClienteRequest
    ): Call<ClienteResponse>


    // Eliminar cliente
    @DELETE("clientes/{id}")
    fun eliminarCliente(
        @Path("id") id: Int
    ): Call<Void>
}