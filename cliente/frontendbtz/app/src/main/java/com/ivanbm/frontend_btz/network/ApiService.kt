package com.ivanbm.frontend_btz.network

import com.ivanbm.frontend_btz.model.AvisoRequest
import com.ivanbm.frontend_btz.model.AvisoResponse
import com.ivanbm.frontend_btz.model.AvisosResponse
import com.ivanbm.frontend_btz.model.ClienteRequest
import com.ivanbm.frontend_btz.model.ClienteResponse
import com.ivanbm.frontend_btz.model.ClientesResponse
import com.ivanbm.frontend_btz.model.LoginRequest
import com.ivanbm.frontend_btz.model.LoginResponse
import com.ivanbm.frontend_btz.model.MaterialRequest
import com.ivanbm.frontend_btz.model.MaterialResponse
import com.ivanbm.frontend_btz.model.MaterialesResponse
import com.ivanbm.frontend_btz.model.TrabajoRequest
import com.ivanbm.frontend_btz.model.TrabajoResponse
import com.ivanbm.frontend_btz.model.TrabajosResponse
import com.ivanbm.frontend_btz.model.UsuarioRequest
import com.ivanbm.frontend_btz.model.UsuarioResponse
import com.ivanbm.frontend_btz.model.UsuariosResponse
import com.ivanbm.frontend_btz.model.FcmTokenRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // LOGIN --------------------------------------------------------------------------------
    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("fcm-token")
    fun guardarTokenFcm(
        @Body request: FcmTokenRequest
    ): Call<Void>


    // CLIENTES ------------------------------------------------------------------------------

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

    // USUARIOS -------------------------------------------------------------------------------

    // Obtener todos los usuarios
    @GET("usuarios")
    fun obtenerUsuarios(
        @Query("page") pagina: Int
    ): Call<UsuariosResponse>

    // Obtener un usuario
    @GET("usuarios/{id}")
    fun obtenerUsuario(
        @Path("id") id: Int
    ): Call<UsuarioResponse>

    // Crear usuario
    @POST("usuarios")
    fun crearUsuario(
        @Body request: UsuarioRequest
    ): Call<UsuarioResponse>

    // Actualizar usuario
    @PUT("usuarios/{id}")
    fun actualizarUsuario(
        @Path("id") id: Int,
        @Body request: UsuarioRequest
    ): Call<UsuarioResponse>

    // Eliminar usuario
    @DELETE("usuarios/{id}")
    fun eliminarUsuario(
        @Path("id") id: Int
    ): Call<Void>

    // AVISOS -----------------------------------------------------------------------------------

    // Obtener avisos
    @GET("avisos")
    fun obtenerAvisos(
        @Query("page") pagina: Int
    ): Call<AvisosResponse>

    // Obtener un aviso
    @GET("avisos/{id}")
    fun obtenerAviso(
        @Path("id") id: Int
    ): Call<AvisoResponse>

    // Crear aviso
    @POST("avisos")
    fun crearAviso(
        @Body request: AvisoRequest
    ): Call<AvisoResponse>

    // Actualizar aviso
    @PUT("avisos/{id}")
    fun actualizarAviso(
        @Path("id") id: Int,
        @Body request: AvisoRequest
    ): Call<AvisoResponse>

    // Eliminar aviso
    @DELETE("avisos/{id}")
    fun eliminarAviso(
        @Path("id") id: Int
    ): Call<Void>

    // MATERIALES --------------------------------------------------------------------------------

    @GET("materiales")
    fun obtenerMateriales(
        @Query("page") pagina: Int
    ): Call<MaterialesResponse>

    @GET("materiales/{id}")
    fun obtenerMaterial(
        @Path("id") id: Int
    ): Call<MaterialResponse>

    @POST("materiales")
    fun crearMaterial(
        @Body request: MaterialRequest
    ): Call<MaterialResponse>

    @PUT("materiales/{id}")
    fun actualizarMaterial(
        @Path("id") id: Int,
        @Body request: MaterialRequest
    ): Call<MaterialResponse>

    @DELETE("materiales/{id}")
    fun eliminarMaterial(
        @Path("id") id: Int
    ): Call<Void>

    // TRABAJOS --------------------------------------------------------------------------------

    // Obtener trabajos asignados al operario
    @GET("trabajos")
    fun obtenerTrabajosAsignados(
        @Query("page") pagina: Int
    ): Call<TrabajosResponse>
    // Crear Trabajo
    @POST("trabajos")
    fun crearTrabajo(
        @Body request: TrabajoRequest
    ): Call<TrabajoResponse>
}