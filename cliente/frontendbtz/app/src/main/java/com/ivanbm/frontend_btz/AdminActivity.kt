package com.ivanbm.frontend_btz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.ivanbm.frontend_btz.adapter.AvisoAdapter
import com.ivanbm.frontend_btz.model.Aviso
import com.ivanbm.frontend_btz.model.AvisosResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class AdminActivity : AppCompatActivity() {

    private lateinit var barraInferiorAdministrador: FrameLayout
    private lateinit var swipeRefreshAvisosAdministrador: SwipeRefreshLayout
    private lateinit var recyclerViewAvisosAdministrador: RecyclerView
    private lateinit var avisoAdapter: AvisoAdapter
    private lateinit var progressBarCargaAvisosAdministrador: ProgressBar

    private lateinit var buttonAvisosPendientesAdministrador: LinearLayout
    private lateinit var buttonAvisosFinalizadosAdministrador: LinearLayout
    private lateinit var buttonBuscarAvisosAdministrador: LinearLayout

    private lateinit var contenedorIconoPendientesAdministrador: FrameLayout
    private lateinit var contenedorIconoFinalizadosAdministrador: FrameLayout
    private lateinit var contenedorIconoBuscarAdministrador: FrameLayout

    private lateinit var indicadorNavegacionAdministrador: View

    private var paginaAvisosActual = 1
    private var cargandoAvisos = false
    private var hayMasAvisos = true

    // Este valor se usa para consultar el backend
    private var estadoFiltroAvisos: String? = "pendiente"

    // Este valor controla la opción resaltada en la barra inferior
    private var opcionNavegacionSeleccionada = "pendiente"

    private var opcionNavegacionAnteriorBuscar = "pendiente"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { vista, insets ->
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
        barraInferiorAdministrador =
            findViewById(R.id.barraInferiorAdministrador)

        swipeRefreshAvisosAdministrador =
            findViewById(R.id.swipeRefreshAvisosAdministrador)

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

        contenedorIconoPendientesAdministrador =
            findViewById(R.id.contenedorIconoPendientesAdministrador)

        contenedorIconoFinalizadosAdministrador =
            findViewById(R.id.contenedorIconoFinalizadosAdministrador)

        contenedorIconoBuscarAdministrador =
            findViewById(R.id.contenedorIconoBuscarAdministrador)

        indicadorNavegacionAdministrador =
            findViewById(R.id.indicadorNavegacionAdministrador)

        actualizarTituloAvisosAdministrador()
        configurarBotonesFiltroAvisos()
        actualizarNavegacionInferiorAdministrador()

        swipeRefreshAvisosAdministrador.setOnRefreshListener {
            recargarAvisos()
        }

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

        recyclerViewAvisosAdministrador.adapter = avisoAdapter

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

                    val totalElementos = layoutManager.itemCount

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

        if (opcionNavegacionSeleccionada == "buscar") {
            opcionNavegacionSeleccionada = opcionNavegacionAnteriorBuscar
            actualizarNavegacionInferiorAdministrador()
        }

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

    private fun mostrarMenuAdministrador(boton: ImageButton) {

        val vistaMenu = layoutInflater.inflate(
            R.layout.menu_opciones_administrador,
            null
        )

        val anchoMenu = (260 * resources.displayMetrics.density).toInt()

        val popupMenu = android.widget.PopupWindow(
            vistaMenu,
            anchoMenu,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupMenu.setBackgroundDrawable(
            android.graphics.drawable.ColorDrawable(
                android.graphics.Color.TRANSPARENT
            )
        )

        popupMenu.elevation = 12 * resources.displayMetrics.density
        popupMenu.isOutsideTouchable = true

        vistaMenu.findViewById<View>(R.id.opcionCrearAvisos)
            .setOnClickListener {
                popupMenu.dismiss()
                startActivity(
                    Intent(this, CrearAvisoActivity::class.java)
                )
            }

        vistaMenu.findViewById<View>(R.id.opcionGestionarMateriales)
            .setOnClickListener {
                popupMenu.dismiss()
                startActivity(
                    Intent(this, MaterialActivity::class.java)
                )
            }

        vistaMenu.findViewById<View>(R.id.opcionGestionarClientes)
            .setOnClickListener {
                popupMenu.dismiss()
                startActivity(
                    Intent(this, ClientesActivity::class.java)
                )
            }

        vistaMenu.findViewById<View>(R.id.opcionGestionarUsuarios)
            .setOnClickListener {
                popupMenu.dismiss()
                startActivity(
                    Intent(this, UsuarioActivity::class.java)
                )
            }

        vistaMenu.findViewById<View>(R.id.opcionCerrarSesion)
            .setOnClickListener {
                popupMenu.dismiss()

                android.app.AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage(
                        "¿Estás seguro de que querés cerrar sesión?"
                    )
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

    private fun actualizarTituloAvisosAdministrador() {

        val textViewTitulo = findViewById<TextView>(
            R.id.textViewTituloAvisosRecientes
        )

        textViewTitulo.text =
            if (estadoFiltroAvisos == "finalizado") {
                "Avisos finalizados"
            } else {
                "Avisos recientes"
            }
    }

    private fun configurarBotonesFiltroAvisos() {

        buttonAvisosPendientesAdministrador.setOnClickListener {

            opcionNavegacionSeleccionada = "pendiente"

            if (estadoFiltroAvisos != "pendiente") {
                estadoFiltroAvisos = "pendiente"
                actualizarTituloAvisosAdministrador()
                recargarAvisos()
            }

            actualizarNavegacionInferiorAdministrador()
        }

        buttonAvisosFinalizadosAdministrador.setOnClickListener {

            opcionNavegacionSeleccionada = "finalizado"

            if (estadoFiltroAvisos != "finalizado") {
                estadoFiltroAvisos = "finalizado"
                actualizarTituloAvisosAdministrador()
                recargarAvisos()
            }

            actualizarNavegacionInferiorAdministrador()
        }

        buttonBuscarAvisosAdministrador.setOnClickListener {
            opcionNavegacionAnteriorBuscar = opcionNavegacionSeleccionada

            if (opcionNavegacionAnteriorBuscar == "buscar") {
                opcionNavegacionAnteriorBuscar =
                    if (estadoFiltroAvisos == "finalizado") "finalizado" else "pendiente"
            }

            opcionNavegacionSeleccionada = "buscar"

            actualizarNavegacionInferiorAdministrador {
                startActivity(
                    Intent(this, BuscarAvisosActivity::class.java)
                )
            }
        }
    }


    private fun actualizarNavegacionInferiorAdministrador(
        alFinalizarAnimacion: (() -> Unit)? = null
    ) {
        val colorMorado = Color.parseColor("#2F2483")
        val colorBlanco = Color.parseColor("#F0F0F0")

        val iconoPendientes = findViewById<ImageView>(
            R.id.iconoPendientesAdministrador
        )
        val iconoFinalizados = findViewById<ImageView>(
            R.id.iconoFinalizadosAdministrador
        )
        val iconoBuscar = findViewById<ImageView>(
            R.id.iconoBuscarAdministrador
        )

        val botones = mapOf(
            "pendiente" to buttonAvisosPendientesAdministrador,
            "finalizado" to buttonAvisosFinalizadosAdministrador,
            "buscar" to buttonBuscarAvisosAdministrador
        )

        val iconos = mapOf(
            "pendiente" to iconoPendientes,
            "finalizado" to iconoFinalizados,
            "buscar" to iconoBuscar
        )

        val textos = mapOf(
            "pendiente" to findViewById<TextView>(
                R.id.textoPendientesAdministrador
            ),
            "finalizado" to findViewById<TextView>(
                R.id.textoFinalizadosAdministrador
            ),
            "buscar" to findViewById<TextView>(
                R.id.textoBuscarAdministrador
            )
        )

        // Actualizar iconos y textos según la opción seleccionada.
        iconos.forEach { (opcion, icono) ->
            val seleccionado = opcion == opcionNavegacionSeleccionada

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

        // Centrar el círculo sobre la opción seleccionada.
        indicadorNavegacionAdministrador.post {
            val botonSeleccionado =
                botones[opcionNavegacionSeleccionada]

            if (botonSeleccionado == null) {
                alFinalizarAnimacion?.invoke()
                return@post
            }

            val centroBoton =
                botonSeleccionado.left + botonSeleccionado.width / 2f

            val posicionX =
                centroBoton - indicadorNavegacionAdministrador.width / 2f

            // Centrar verticalmente el círculo en la barra.
            val posicionY = (barraInferiorAdministrador.height -
                    indicadorNavegacionAdministrador.height) / 2f + 6f

            indicadorNavegacionAdministrador.animate().cancel()

            if (
                indicadorNavegacionAdministrador.visibility != View.VISIBLE
            ) {
                indicadorNavegacionAdministrador.translationX = posicionX
                indicadorNavegacionAdministrador.translationY = posicionY
                indicadorNavegacionAdministrador.visibility = View.VISIBLE

                alFinalizarAnimacion?.invoke()
                return@post
            }

            indicadorNavegacionAdministrador.animate()
                .translationX(posicionX)
                .translationY(posicionY)
                .setDuration(180)
                .withEndAction {
                    alFinalizarAnimacion?.invoke()
                }
                .start()
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