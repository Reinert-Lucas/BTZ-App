package com.ivanbm.frontend_btz.model

data class TrabajoRequest(
    val trabajo_realizado: String,
    val desperfecto: String,
    val aviso_id: Int,
    val materiales: List<MaterialTrabajo>
)