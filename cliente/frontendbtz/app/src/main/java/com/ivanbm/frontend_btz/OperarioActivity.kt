package com.ivanbm.frontend_btz

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.ivanbm.frontend_btz.adapter.AvisoAdapter
import com.ivanbm.frontend_btz.model.TrabajosResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import com.ivanbm.frontend_btz.network.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OperarioActivity : AppCompatActivity() {

    private lateinit var barraInferiorOperario: FrameLayout
    private lateinit var swipeRefreshAvisosOperario: SwipeRefreshLayout
    private lateinit var recyclerViewAvisosOperario: RecyclerView
    private lateinit var progressBarCargaAvisosOperario: ProgressBar
    private lateinit var avisoAdapter: AvisoAdapter

    private lateinit var buttonAvisosPendientesOperario: LinearLayout
    private lateinit var buttonAvisosFinalizadosOperario: LinearLayout
    private lateinit var buttonBuscarAvisosOperario: LinearLayout

    private lateinit var indicadorNavegacionOperario: View

    private var paginaAvisosActual = 1
    private var cargandoAvisos = false
    private var hayMasAvisos = true
    private var primeraCargaAvisos = true

    private var mostrarFinalizados = false

    private var opcionNavegacionSeleccionada = "pendiente"
    private var opcionNavegacionAnteriorBuscar = "pendiente"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        SessionManager.inicializar(applicationContext)
        setContentView(R.layout.activity_operario)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { vista, insets ->

            val barrasSistema = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )

            vista.setPadding(
                barrasSistema.left,
                barrasSistema.top,
                barrasSistema.right,
                barrasSistema.bottom
            )

            insets
        }

        barraInferiorOperario =
            findViewById(R.id.barraInferiorOperario)

        swipeRefreshAvisosOperario =
            findViewById(R.id.swipeRefreshAvisosOperario)

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

        indicadorNavegacionOperario =
            findViewById(R.id.indicadorNavegacionOperario)

        configurarRecyclerView()
        configurarBotonesFiltro()
        actualizarNavegacionInferiorOperario()

        swipeRefreshAvisosOperario.setOnRefreshListener {
            reiniciarListaAvisos()
            cargarAvisosOperario(1)
        }

        val buttonMenuOperario = findViewById<ImageButton>(
            R.id.buttonMenuOperario
        )

        buttonMenuOperario.setOnClickListener {
            mostrarMenuOperario(buttonMenuOperario)
        }

        cargarAvisosOperario(1)
    }

    override fun onResume() {
        super.onResume()

        if (primeraCargaAvisos) {
            primeraCargaAvisos = false
            return
        }

        if (opcionNavegacionSeleccionada == "buscar") {
            opcionNavegacionSeleccionada =
                opcionNavegacionAnteriorBuscar
            actualizarNavegacionInferiorOperario()
        }

        reiniciarListaAvisos()
        cargarAvisosOperario(1)
    }

    private fun configurarRecyclerView() {

        avisoAdapter = AvisoAdapter(
            emptyList(),
            onAvisoClick = { aviso ->

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
                intent.putExtra(
                    "AVISO_FINALIZADO",
                    mostrarFinalizados
                )

                if (mostrarFinalizados && aviso.trabajo != null) {
                    intent.putExtra(
                        "AVISO_TRABAJO_JSON",
                        Gson().toJson(aviso.trabajo)
                    )
                }

                startActivity(intent)
            }
        )

        recyclerViewAvisosOperario.layoutManager =
            LinearLayoutManager(this)

        recyclerViewAvisosOperario.adapter = avisoAdapter

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

            opcionNavegacionSeleccionada = "pendiente"

            if (mostrarFinalizados) {
                mostrarFinalizados = false
                actualizarTitulo()
                reiniciarListaAvisos()
                cargarAvisosOperario(1)
            }

            actualizarNavegacionInferiorOperario()
        }

        buttonAvisosFinalizadosOperario.setOnClickListener {

            opcionNavegacionSeleccionada = "finalizado"

            if (!mostrarFinalizados) {
                mostrarFinalizados = true
                actualizarTitulo()
                reiniciarListaAvisos()
                cargarAvisosOperario(1)
            }

            actualizarNavegacionInferiorOperario()
        }

        buttonBuscarAvisosOperario.setOnClickListener {

            opcionNavegacionAnteriorBuscar =
                if (mostrarFinalizados) {
                    "finalizado"
                } else {
                    "pendiente"
                }

            opcionNavegacionSeleccionada = "buscar"

            actualizarNavegacionInferiorOperario {
                val intent = Intent(
                    this,
                    BuscarAvisosActivity::class.java
                )

                intent.putExtra("MODO_BUSQUEDA", "OPERARIO")
                startActivity(intent)
            }
        }
    }

    private fun actualizarTitulo() {

        findViewById<TextView>(
            R.id.textViewTituloAvisosOperario
        ).text = if (mostrarFinalizados) {
            "Avisos finalizados"
        } else {
            "Avisos asignados"
        }
    }

    private fun actualizarNavegacionInferiorOperario(
        alFinalizarAnimacion: (() -> Unit)? = null
    ) {

        val colorMorado = Color.parseColor("#2F2483")
        val colorBlanco = Color.parseColor("#F0F0F0")

        val iconoPendientes = findViewById<ImageView>(
            R.id.iconoPendientesOperario
        )

        val iconoFinalizados = findViewById<ImageView>(
            R.id.iconoFinalizadosOperario
        )

        val iconoBuscar = findViewById<ImageView>(
            R.id.iconoBuscarOperario
        )

        val botones = mapOf(
            "pendiente" to buttonAvisosPendientesOperario,
            "finalizado" to buttonAvisosFinalizadosOperario,
            "buscar" to buttonBuscarAvisosOperario
        )

        val iconos = mapOf(
            "pendiente" to iconoPendientes,
            "finalizado" to iconoFinalizados,
            "buscar" to iconoBuscar
        )

        val textos = mapOf(
            "pendiente" to findViewById<TextView>(
                R.id.textoPendientesOperario
            ),
            "finalizado" to findViewById<TextView>(
                R.id.textoFinalizadosOperario
            ),
            "buscar" to findViewById<TextView>(
                R.id.textoBuscarOperario
            )
        )

        iconos.forEach { (opcion, icono) ->

            val seleccionado =
                opcion == opcionNavegacionSeleccionada

            icono.animate().cancel()

            icono.setColorFilter(
                if (seleccionado) colorBlanco else colorMorado
            )

            icono.animate()
                .scaleX(if (seleccionado) 1.15f else 1f)
                .scaleY(if (seleccionado) 1.15f else 1f)
                .setDuration(120)
                .start()
        }

        textos.forEach { (opcion, texto) ->

            texto.setTextColor(
                if (opcion == opcionNavegacionSeleccionada) {
                    colorBlanco
                } else {
                    colorMorado
                }
            )
        }

        indicadorNavegacionOperario.post {

            val botonSeleccionado =
                botones[opcionNavegacionSeleccionada]

            if (botonSeleccionado == null) {
                alFinalizarAnimacion?.invoke()
                return@post
            }

            val centroBoton =
                botonSeleccionado.left +
                        botonSeleccionado.width / 2f

            val posicionX =
                centroBoton -
                        indicadorNavegacionOperario.width / 2f

            val posicionY =
                (barraInferiorOperario.height -
                        indicadorNavegacionOperario.height) / 2f + 6f

            indicadorNavegacionOperario.animate().cancel()

            if (
                indicadorNavegacionOperario.visibility != View.VISIBLE
            ) {
                indicadorNavegacionOperario.translationX = posicionX
                indicadorNavegacionOperario.translationY = posicionY
                indicadorNavegacionOperario.visibility = View.VISIBLE

                alFinalizarAnimacion?.invoke()
                return@post
            }

            indicadorNavegacionOperario.animate()
                .translationX(posicionX)
                .translationY(posicionY)
                .setDuration(180)
                .withEndAction {
                    alFinalizarAnimacion?.invoke()
                }
                .start()
        }
    }

    private fun reiniciarListaAvisos() {
        paginaAvisosActual = 1
        cargandoAvisos = false
        hayMasAvisos = true
        avisoAdapter.actualizarAvisos(emptyList())
    }

    private fun cargarAvisosOperario(pagina: Int) {

        if (cargandoAvisos || !hayMasAvisos) {
            swipeRefreshAvisosOperario.isRefreshing = false
            return
        }

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
            RetrofitClient.api.obtenerTrabajosFinalizados(
                usuarioId,
                pagina
            )
        } else {
            RetrofitClient.api.obtenerTrabajosAsignados(pagina)
        }

        llamada.enqueue(object : Callback<TrabajosResponse> {

            override fun onResponse(
                call: Call<TrabajosResponse>,
                response: Response<TrabajosResponse>
            ) {

                cargandoAvisos = false

                progressBarCargaAvisosOperario.visibility =
                    View.GONE

                swipeRefreshAvisosOperario.isRefreshing = false

                if (response.isSuccessful && response.body() != null) {

                    val respuesta = response.body()!!

                    if (respuesta.status) {

                        if (pagina == 1) {
                            avisoAdapter.actualizarAvisos(
                                respuesta.data
                            )
                        } else {
                            avisoAdapter.agregarAvisos(
                                respuesta.data
                            )
                        }

                        paginaAvisosActual =
                            respuesta.meta.current_page

                        hayMasAvisos =
                            respuesta.meta.current_page <
                                    respuesta.meta.last_page

                        Log.d(
                            "OPERARIO_AVISOS",
                            "Filtro finalizados=$mostrarFinalizados, " +
                                    "página=${respuesta.meta.current_page}/" +
                                    "${respuesta.meta.last_page}"
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

                progressBarCargaAvisosOperario.visibility =
                    View.GONE

                swipeRefreshAvisosOperario.isRefreshing = false

                Log.e(
                    "OperarioActivity",
                    "Error al cargar avisos",
                    t
                )

                Toast.makeText(
                    this@OperarioActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    private fun mostrarMenuOperario(boton: ImageButton) {
        val vistaMenu = layoutInflater.inflate(
            R.layout.menu_opciones_operario,
            null
        )

        val anchoMenu = (260 * resources.displayMetrics.density).toInt()

        val popupMenu = PopupWindow(
            vistaMenu,
            anchoMenu,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupMenu.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        popupMenu.elevation = 12 * resources.displayMetrics.density
        popupMenu.isOutsideTouchable = true

        vistaMenu.findViewById<View>(R.id.opcionCerrarSesion)
            .setOnClickListener {
                popupMenu.dismiss()

                AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que querés cerrar sesión?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Cerrar sesión") { _, _ ->
                        CerrarSesion.ejecutar(this)
                    }
                    .show()
            }

        popupMenu.showAsDropDown(
            boton,
            boton.width - anchoMenu,
            8
        )
    }
}