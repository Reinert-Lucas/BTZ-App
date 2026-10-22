package com.ivanbm.frontend_btz

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.adapter.AvisoAdapter
import com.ivanbm.frontend_btz.model.AvisosResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.jvm.java
import com.google.gson.Gson
import com.ivanbm.frontend_btz.model.Aviso
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class AdminActivity : AppCompatActivity() {

    private lateinit var swipeRefreshAvisosAdministrador: SwipeRefreshLayout
    private lateinit var recyclerViewAvisosAdministrador: RecyclerView
    private lateinit var avisoAdapter: AvisoAdapter
    private lateinit var progressBarCargaAvisosAdministrador: ProgressBar

    private var paginaAvisosActual = 1

    private var cargandoAvisos = false

    private var hayMasAvisos = true

    private lateinit var buttonAvisosPendientesAdministrador: android.widget.Button
    private lateinit var buttonAvisosFinalizadosAdministrador: android.widget.Button
    private lateinit var buttonBuscarAvisosAdministrador: android.widget.Button
    private var estadoFiltroAvisos: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_admin)

        swipeRefreshAvisosAdministrador =
            findViewById(R.id.swipeRefreshAvisosAdministrador)

        swipeRefreshAvisosAdministrador.setOnRefreshListener {
            recargarAvisos()
        }

        recyclerViewAvisosAdministrador =
            findViewById(R.id.recyclerViewAvisosAdministrador)

        progressBarCargaAvisosAdministrador =
            findViewById(R.id.progressBarCargaAvisosAdministrador)

        buttonAvisosPendientesAdministrador =
            findViewById(R.id.buttonAvisosPendientesAdministrador)

        buttonAvisosFinalizadosAdministrador =
            findViewById(R.id.buttonAvisosFinalizadosAdministrador)

        buttonBuscarAvisosAdministrador =
            findViewById(R.id.buttonBuscarAvisosAdministrador)

        actualizarTituloAvisosAdministrador()
        configurarBotonesFiltroAvisos()

        avisoAdapter = AvisoAdapter(
            emptyList(),
            onAvisoClick = { aviso ->
                if (aviso.estado == "finalizado") {
                    abrirTrabajoFinalizado(aviso)
                } else {
                    val intent = Intent(
                        this,
                        EditarAvisoActivity::class.java
                    )

                    intent.putExtra("AVISO_ID", aviso.id)
                    startActivity(intent)
                }
            }
        )

        recyclerViewAvisosAdministrador.layoutManager =
            LinearLayoutManager(this)

        recyclerViewAvisosAdministrador.adapter =
            avisoAdapter

        recyclerViewAvisosAdministrador.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy <= 0) {
                        return
                    }

                    val layoutManager =
                        recyclerView.layoutManager as LinearLayoutManager

                    val totalElementos =
                        layoutManager.itemCount

                    val ultimoElementoVisible =
                        layoutManager.findLastVisibleItemPosition()

                    if (
                        ultimoElementoVisible >= totalElementos - 1 &&
                        !cargandoAvisos &&
                        hayMasAvisos
                    ) {
                        cargarAvisos()
                    }
                }
            }
        )
        val buttonMenuAdministrador =
            findViewById<ImageButton>(R.id.buttonMenuAdministrador)

        buttonMenuAdministrador.setOnClickListener {
            mostrarMenuAdministrador(buttonMenuAdministrador)
        }

        cargarAvisos()
    }
    override fun onResume() {
        super.onResume()

        recargarAvisos()
    }
    private fun cargarAvisos() {

        if (cargandoAvisos || !hayMasAvisos) {
            return
        }

        cargandoAvisos = true

        progressBarCargaAvisosAdministrador.visibility =
            ProgressBar.VISIBLE

        val llamadaAvisos: Call<AvisosResponse> =
            if (estadoFiltroAvisos == "finalizado") {
                RetrofitClient.api.obtenerTodosLosTrabajosFinalizados(
                    paginaAvisosActual
                )
            } else {
                RetrofitClient.api.obtenerAvisos(
                    paginaAvisosActual,
                    estadoFiltroAvisos
                )
            }

        llamadaAvisos.enqueue(object : Callback<AvisosResponse> {
                override fun onResponse(
                    call: Call<AvisosResponse>,
                    response: Response<AvisosResponse>
                ) {

                    progressBarCargaAvisosAdministrador.visibility =
                        ProgressBar.GONE

                    cargandoAvisos = false

                    swipeRefreshAvisosAdministrador.isRefreshing = false
                    if (!response.isSuccessful) {

                        Toast.makeText(
                            this@AdminActivity,
                            "Error HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    val respuesta = response.body()

                    if (respuesta != null && respuesta.status) {

                        if (paginaAvisosActual == 1) {

                            avisoAdapter.actualizarAvisos(
                                respuesta.data
                            )

                        } else {

                            avisoAdapter.agregarAvisos(
                                respuesta.data
                            )
                        }

                        hayMasAvisos =
                            respuesta.meta.current_page <
                                    respuesta.meta.last_page

                        if (hayMasAvisos) {
                            paginaAvisosActual++
                        }

                    } else {

                        Toast.makeText(
                            this@AdminActivity,
                            "No se pudieron obtener los avisos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<AvisosResponse>,
                    t: Throwable
                ) {

                    progressBarCargaAvisosAdministrador.visibility =
                        ProgressBar.GONE

                    cargandoAvisos = false

                    swipeRefreshAvisosAdministrador.isRefreshing = false

                    Toast.makeText(
                        this@AdminActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
    private fun recargarAvisos() {

        paginaAvisosActual = 1
        hayMasAvisos = true
        cargandoAvisos = false

        avisoAdapter.actualizarAvisos(emptyList())

        cargarAvisos()
    }
    private fun mostrarMenuAdministrador(
        boton: ImageButton
    ) {

        val popupMenu = android.widget.PopupMenu(
            this,
            boton
        )

        popupMenu.menu.add("Crear avisos")
        popupMenu.menu.add("Gestionar herramientas y materiales")
        popupMenu.menu.add("Gestionar clientes")
        popupMenu.menu.add("Gestionar usuarios")
        popupMenu.menu.add("Cerrar sesión")

        popupMenu.setOnMenuItemClickListener { item ->

            when (item.title.toString()) {

                "Crear avisos" -> {

                    startActivity(
                        Intent(
                            this,
                            CrearAvisoActivity::class.java
                        )
                    )

                    true
                }

                "Gestionar clientes" -> {

                    startActivity(
                        Intent(
                            this,
                            ClientesActivity::class.java
                        )
                    )

                    true
                }

                "Gestionar usuarios" -> {

                    startActivity(
                        Intent(
                            this,
                            PersonalActivity::class.java
                        )
                    )

                    true
                }

                "Gestionar herramientas y materiales" -> {
                    startActivity(
                        Intent(
                            this,
                            MaterialActivity::class.java
                        )
                    )
                    true
                }

                "Cerrar sesión" -> {
                    android.app.AlertDialog.Builder(this)
                        .setTitle("Cerrar sesión")
                        .setMessage("¿Estás seguro de que querés cerrar sesión?")
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Cerrar sesión") { _, _ ->
                            CerrarSesion.ejecutar(this)
                        }
                        .show()

                    true
                }

                else -> {
                    true
                }
            }
        }

        popupMenu.show()
    }

    private fun actualizarTituloAvisosAdministrador() {
        val textViewTitulo = findViewById<android.widget.TextView>(
            R.id.textViewTituloAvisosRecientes
        )

        textViewTitulo.text = if (estadoFiltroAvisos == "finalizado") {
            "Avisos finalizados"
        } else {
            "Avisos recientes"
        }
    }
    private fun configurarBotonesFiltroAvisos() {

        buttonAvisosPendientesAdministrador.setOnClickListener {
            if (estadoFiltroAvisos != "pendiente") {
                estadoFiltroAvisos = "pendiente"
                actualizarTituloAvisosAdministrador()
                recargarAvisos()
            }
        }

        buttonAvisosFinalizadosAdministrador.setOnClickListener {
            if (estadoFiltroAvisos != "finalizado") {
                estadoFiltroAvisos = "finalizado"
                actualizarTituloAvisosAdministrador()
                recargarAvisos()
            }
        }

        buttonBuscarAvisosAdministrador.setOnClickListener {
            val intent = Intent(
                this,
                BuscarAvisosActivity::class.java
            )

            startActivity(intent)
        }
    }
    private fun abrirTrabajoFinalizado(aviso: Aviso) {
        if (aviso.trabajo == null) {
            Toast.makeText(
                this,
                "No se encontraron los detalles del trabajo finalizado",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val intent = Intent(
            this,
            TrabajoActivity::class.java
        )

        intent.putExtra("AVISO_ID", aviso.id)
        intent.putExtra("AVISO_FECHA", aviso.fecha)
        intent.putExtra("AVISO_HORA", aviso.hora)
        intent.putExtra("AVISO_DIRECCION", aviso.direccion)
        intent.putExtra("AVISO_TELEFONO", aviso.telefono)
        intent.putExtra("AVISO_MENSAJE", aviso.mensaje)
        intent.putExtra("AVISO_OBSERVACION", aviso.observacion)
        intent.putExtra("AVISO_URGENCIA", aviso.urgencia)
        intent.putExtra("AVISO_CLIENTE", aviso.cliente.nombre)
        intent.putExtra("AVISO_FINALIZADO", true)
        intent.putExtra(
            "AVISO_TRABAJO_JSON",
            Gson().toJson(aviso.trabajo)
        )

        startActivity(intent)
    }
}