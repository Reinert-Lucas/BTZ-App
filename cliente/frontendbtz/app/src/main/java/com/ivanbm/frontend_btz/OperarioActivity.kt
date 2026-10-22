package com.ivanbm.frontend_btz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.adapter.AvisoAdapter
import com.ivanbm.frontend_btz.model.TrabajosResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import com.ivanbm.frontend_btz.network.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson
import android.widget.ImageButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class OperarioActivity : AppCompatActivity() {

    private lateinit var swipeRefreshAvisosOperario: SwipeRefreshLayout
    private lateinit var recyclerViewAvisosOperario: RecyclerView
    private lateinit var progressBarCargaAvisosOperario: ProgressBar
    private lateinit var buttonAvisosPendientesOperario: Button
    private lateinit var buttonAvisosFinalizadosOperario: Button
    private lateinit var buttonBuscarAvisosOperario: Button
    private lateinit var avisoAdapter: AvisoAdapter

    private var paginaAvisosActual = 1
    private var cargandoAvisos = false
    private var hayMasAvisos = true
    private var primeraCargaAvisos = true

    private var mostrarFinalizados = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SessionManager.inicializar(applicationContext)
        setContentView(R.layout.activity_operario)

        swipeRefreshAvisosOperario =
            findViewById(R.id.swipeRefreshAvisosOperario)

        swipeRefreshAvisosOperario.setOnRefreshListener {
            reiniciarListaAvisos()
            cargarAvisosOperario(1)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { vista, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        recyclerViewAvisosOperario =
            findViewById(R.id.recyclerViewAvisosOperario)

        progressBarCargaAvisosOperario =
            findViewById(R.id.progressBarCargaAvisosOperario)

        buttonAvisosPendientesOperario =
            findViewById(R.id.buttonAvisosPendientesOperario)

        buttonAvisosFinalizadosOperario =
            findViewById(R.id.buttonAvisosFinalizadosOperario)

        buttonBuscarAvisosOperario =
            findViewById(R.id.buttonBuscarAvisosOperario)

        val buttonMenuOperario = findViewById<ImageButton>(
            R.id.buttonMenuOperario
        )

        buttonMenuOperario.setOnClickListener {
            mostrarMenuOperario(buttonMenuOperario)
        }

        configurarRecyclerView()
        configurarBotonesFiltro()
        cargarAvisosOperario(1)
    }

    override fun onResume() {
        super.onResume()

        if (primeraCargaAvisos) {
            primeraCargaAvisos = false
            return
        }

        reiniciarListaAvisos()
        cargarAvisosOperario(1)
    }

    private fun configurarRecyclerView() {
        avisoAdapter = AvisoAdapter(
            emptyList(),
            onAvisoClick = { aviso ->
                val intent = Intent(this, TrabajoActivity::class.java)

                intent.putExtra("AVISO_ID", aviso.id)
                intent.putExtra("AVISO_FECHA", aviso.fecha)
                intent.putExtra("AVISO_HORA", aviso.hora)
                intent.putExtra("AVISO_DIRECCION", aviso.direccion)
                intent.putExtra("AVISO_TELEFONO", aviso.telefono)
                intent.putExtra("AVISO_MENSAJE", aviso.mensaje)
                intent.putExtra("AVISO_OBSERVACION", aviso.observacion)
                intent.putExtra("AVISO_URGENCIA", aviso.urgencia)
                intent.putExtra("AVISO_CLIENTE", aviso.cliente.nombre)
                intent.putExtra("AVISO_FINALIZADO", mostrarFinalizados)

                if (mostrarFinalizados && aviso.trabajo != null) {
                    intent.putExtra(
                        "AVISO_TRABAJO_JSON",
                        Gson().toJson(aviso.trabajo)
                    )
                }
                startActivity(intent)
            }
        )

        recyclerViewAvisosOperario.adapter = avisoAdapter
        recyclerViewAvisosOperario.layoutManager = LinearLayoutManager(this)

        recyclerViewAvisosOperario.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy <= 0) return

                    val layoutManager =
                        recyclerView.layoutManager as LinearLayoutManager

                    val ultimaPosicionVisible =
                        layoutManager.findLastVisibleItemPosition()

                    val cantidadElementos = avisoAdapter.itemCount

                    if (
                        ultimaPosicionVisible >= cantidadElementos - 2 &&
                        !cargandoAvisos &&
                        hayMasAvisos
                    ) {
                        cargarAvisosOperario(paginaAvisosActual + 1)
                    }
                }
            }
        )
    }

    private fun configurarBotonesFiltro() {
        buttonAvisosPendientesOperario.setOnClickListener {
            if (mostrarFinalizados) {
                mostrarFinalizados = false
                actualizarTitulo()
                reiniciarListaAvisos()
                cargarAvisosOperario(1)
            }
        }

        buttonAvisosFinalizadosOperario.setOnClickListener {
            if (!mostrarFinalizados) {
                mostrarFinalizados = true
                actualizarTitulo()
                reiniciarListaAvisos()
                cargarAvisosOperario(1)
            }
        }

        buttonBuscarAvisosOperario.setOnClickListener {
            val intent = Intent(this, BuscarAvisosActivity::class.java)
            intent.putExtra("MODO_BUSQUEDA", "OPERARIO")
            startActivity(intent)
        }

    }

    private fun actualizarTitulo() {
        findViewById<android.widget.TextView>(
            R.id.textViewTituloAvisosOperario
        ).text = if (mostrarFinalizados) {
            "Avisos finalizados"
        } else {
            "Avisos asignados"
        }
    }

    private fun reiniciarListaAvisos() {
        paginaAvisosActual = 1
        cargandoAvisos = false
        hayMasAvisos = true
        avisoAdapter.actualizarAvisos(emptyList())
    }

    private fun cargarAvisosOperario(pagina: Int) {
        if (cargandoAvisos || !hayMasAvisos) return

        val usuarioId = SessionManager.obtenerUsuarioId()

        if (mostrarFinalizados && usuarioId <= 0) {
            swipeRefreshAvisosOperario.isRefreshing = false
            Toast.makeText(
                this,
                "No se pudo identificar al operario. Cerrá sesión e ingresá nuevamente.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        cargandoAvisos = true
        progressBarCargaAvisosOperario.visibility = View.VISIBLE

        val llamada = if (mostrarFinalizados) {
            RetrofitClient.api.obtenerTrabajosFinalizados(usuarioId, pagina)
        } else {
            RetrofitClient.api.obtenerTrabajosAsignados(pagina)
        }

        llamada.enqueue(object : Callback<TrabajosResponse> {

            override fun onResponse(
                call: Call<TrabajosResponse>,
                response: Response<TrabajosResponse>
            ) {
                cargandoAvisos = false
                progressBarCargaAvisosOperario.visibility = View.GONE
                swipeRefreshAvisosOperario.isRefreshing = false

                if (response.isSuccessful && response.body() != null) {
                    val respuesta = response.body()!!

                    if (respuesta.status) {
                        if (pagina == 1) {
                            avisoAdapter.actualizarAvisos(respuesta.data)
                        } else {
                            avisoAdapter.agregarAvisos(respuesta.data)
                        }

                        paginaAvisosActual = respuesta.meta.current_page
                        hayMasAvisos =
                            respuesta.meta.current_page < respuesta.meta.last_page

                        Log.d(
                            "OPERARIO_AVISOS",
                            "Filtro finalizados=$mostrarFinalizados, página=${respuesta.meta.current_page}/${respuesta.meta.last_page}"
                        )
                    } else {
                        Toast.makeText(
                            this@OperarioActivity,
                            respuesta.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@OperarioActivity,
                        "Error al cargar los avisos (HTTP ${response.code()})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<TrabajosResponse>,
                t: Throwable
            ) {
                cargandoAvisos = false
                progressBarCargaAvisosOperario.visibility = View.GONE
                swipeRefreshAvisosOperario.isRefreshing = false
                Toast.makeText(
                    this@OperarioActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    private fun mostrarMenuOperario(boton: ImageButton) {
        val popupMenu = android.widget.PopupMenu(this, boton)

        popupMenu.menu.add("Cerrar sesión")

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.title.toString()) {
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

                else -> false
            }
        }

        popupMenu.show()
    }
}
