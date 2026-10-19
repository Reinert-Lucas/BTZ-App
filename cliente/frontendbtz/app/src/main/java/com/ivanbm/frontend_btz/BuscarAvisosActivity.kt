package com.ivanbm.frontend_btz

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.ivanbm.frontend_btz.adapter.AvisoAdapter
import com.ivanbm.frontend_btz.model.Aviso
import com.ivanbm.frontend_btz.model.AvisosResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BuscarAvisosActivity : AppCompatActivity() {

    private lateinit var buttonVolverBuscarAvisos: ImageButton
    private lateinit var buttonMostrarFiltrosAvisos: Button
    private lateinit var buttonEjecutarBusquedaAvisos: Button
    private lateinit var buttonAplicarFiltrosAvisos: Button
    private lateinit var buttonLimpiarFiltrosAvisos: Button

    private lateinit var scrollViewFiltrosAvisos: ScrollView
    private lateinit var editTextBusquedaGeneralAvisos: EditText
    private lateinit var editTextDireccionFiltroAvisos: EditText
    private lateinit var editTextTelefonoFiltroAvisos: EditText
    private lateinit var editTextNumeroAvisoFiltro: EditText
    private lateinit var editTextFechaFiltroAvisos: EditText
    private lateinit var editTextHoraFiltroAvisos: EditText
    private lateinit var radioGroupEstadoFiltroAvisos: RadioGroup

    private lateinit var recyclerViewResultadosBuscarAvisos: RecyclerView
    private lateinit var progressBarBuscarAvisos: ProgressBar
    private lateinit var avisoAdapter: AvisoAdapter

    private var paginaActual = 1
    private var cargandoAvisos = false
    private var hayMasAvisos = true
    private var busquedaActual: FiltrosBusqueda? = null

    private data class FiltrosBusqueda(
        val buscar: String? = null,
        val direccion: String? = null,
        val telefono: String? = null,
        val avisoId: Int? = null,
        val fecha: String? = null,
        val hora: String? = null,
        val estado: String? = null
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_avisos)

        inicializarVistas()
        configurarRecyclerView()
        configurarBotonVolver()
        configurarFiltrosAvanzados()
        configurarSelectorFecha()
        configurarSelectorHora()
        configurarBotonesBusqueda()
        configurarPaginacion()
    }

    private fun inicializarVistas() {
        buttonVolverBuscarAvisos =
            findViewById(R.id.buttonVolverBuscarAvisos)

        buttonMostrarFiltrosAvisos =
            findViewById(R.id.buttonMostrarFiltrosAvisos)

        buttonEjecutarBusquedaAvisos =
            findViewById(R.id.buttonEjecutarBusquedaAvisos)

        buttonAplicarFiltrosAvisos =
            findViewById(R.id.buttonAplicarFiltrosAvisos)

        buttonLimpiarFiltrosAvisos =
            findViewById(R.id.buttonLimpiarFiltrosAvisos)

        scrollViewFiltrosAvisos =
            findViewById(R.id.scrollViewFiltrosAvisos)

        editTextBusquedaGeneralAvisos =
            findViewById(R.id.editTextBusquedaGeneralAvisos)

        editTextDireccionFiltroAvisos =
            findViewById(R.id.editTextDireccionFiltroAvisos)

        editTextTelefonoFiltroAvisos =
            findViewById(R.id.editTextTelefonoFiltroAvisos)

        editTextNumeroAvisoFiltro =
            findViewById(R.id.editTextNumeroAvisoFiltro)

        editTextFechaFiltroAvisos =
            findViewById(R.id.editTextFechaFiltroAvisos)

        editTextHoraFiltroAvisos =
            findViewById(R.id.editTextHoraFiltroAvisos)

        radioGroupEstadoFiltroAvisos =
            findViewById(R.id.radioGroupEstadoFiltroAvisos)

        recyclerViewResultadosBuscarAvisos =
            findViewById(R.id.recyclerViewResultadosBuscarAvisos)

        progressBarBuscarAvisos =
            findViewById(R.id.progressBarBuscarAvisos)
    }

    private fun configurarRecyclerView() {
        avisoAdapter = AvisoAdapter(
            emptyList(),
            onAvisoClick = { aviso ->
                abrirAvisoSeleccionado(aviso)
            }
        )

        recyclerViewResultadosBuscarAvisos.layoutManager =
            LinearLayoutManager(this)

        recyclerViewResultadosBuscarAvisos.adapter = avisoAdapter
    }

    private fun configurarBotonVolver() {
        buttonVolverBuscarAvisos.setOnClickListener {
            finish()
        }
    }

    private fun configurarFiltrosAvanzados() {
        buttonMostrarFiltrosAvisos.setOnClickListener {
            val mostrarFiltros =
                scrollViewFiltrosAvisos.visibility != View.VISIBLE

            scrollViewFiltrosAvisos.visibility =
                if (mostrarFiltros) View.VISIBLE else View.GONE

            buttonMostrarFiltrosAvisos.text =
                if (mostrarFiltros) "Filtros avanzados ▲"
                else "Filtros avanzados ▼"

            val parametrosResultados =
                recyclerViewResultadosBuscarAvisos.layoutParams as
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams

            val parametrosCarga =
                progressBarBuscarAvisos.layoutParams as
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams

            val vistaSuperior = if (mostrarFiltros) {
                R.id.scrollViewFiltrosAvisos
            } else {
                R.id.buttonMostrarFiltrosAvisos
            }

            parametrosResultados.topToBottom = vistaSuperior
            parametrosCarga.topToBottom = vistaSuperior

            recyclerViewResultadosBuscarAvisos.layoutParams =
                parametrosResultados

            progressBarBuscarAvisos.layoutParams =
                parametrosCarga
        }
    }

    private fun configurarBotonesBusqueda() {
        buttonEjecutarBusquedaAvisos.setOnClickListener {
            val termino = editTextBusquedaGeneralAvisos.text
                .toString()
                .trim()

            if (termino.isEmpty()) {
                editTextBusquedaGeneralAvisos.error =
                    "Ingresá qué aviso querés buscar"
                return@setOnClickListener
            }

            val filtros = FiltrosBusqueda(
                buscar = termino,
                estado = obtenerEstadoSeleccionado()
            )

            iniciarBusqueda(filtros)
        }

        buttonAplicarFiltrosAvisos.setOnClickListener {
            val filtros = crearFiltrosAvanzados() ?: return@setOnClickListener

            if (filtros.buscar == null &&
                filtros.direccion == null &&
                filtros.telefono == null &&
                filtros.avisoId == null &&
                filtros.fecha == null &&
                filtros.hora == null &&
                filtros.estado == null
            ) {
                Toast.makeText(
                    this,
                    "Seleccioná al menos un criterio de búsqueda",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            iniciarBusqueda(filtros)
        }

        buttonLimpiarFiltrosAvisos.setOnClickListener {
            limpiarCamposBusqueda()
            iniciarBusqueda(FiltrosBusqueda())
        }
    }

    private fun crearFiltrosAvanzados(): FiltrosBusqueda? {
        val termino = editTextBusquedaGeneralAvisos.text
            .toString()
            .trim()
            .takeIf { it.isNotEmpty() }

        val direccion = editTextDireccionFiltroAvisos.text
            .toString()
            .trim()
            .takeIf { it.isNotEmpty() }

        val telefono = editTextTelefonoFiltroAvisos.text
            .toString()
            .trim()
            .takeIf { it.isNotEmpty() }

        val numeroTexto = editTextNumeroAvisoFiltro.text
            .toString()
            .trim()

        val numeroAviso = if (numeroTexto.isNotEmpty()) {
            numeroTexto.toIntOrNull() ?: run {
                editTextNumeroAvisoFiltro.error =
                    "Ingresá un número de aviso válido"
                return null
            }
        } else {
            null
        }

        val fechaTexto = editTextFechaFiltroAvisos.text
            .toString()
            .trim()

        val fechaApi = convertirFechaParaApi(fechaTexto)

        if (fechaTexto.isNotEmpty() && fechaApi == null) {
            Toast.makeText(
                this,
                "La fecha seleccionada no es válida",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }

        val hora = editTextHoraFiltroAvisos.text
            .toString()
            .trim()
            .takeIf { it.isNotEmpty() }

        return FiltrosBusqueda(
            buscar = termino,
            direccion = direccion,
            telefono = telefono,
            avisoId = numeroAviso,
            fecha = fechaApi,
            hora = hora,
            estado = obtenerEstadoSeleccionado()
        )
    }

    private fun obtenerEstadoSeleccionado(): String? {
        return when (radioGroupEstadoFiltroAvisos.checkedRadioButtonId) {
            R.id.radioButtonPendientesFiltroAvisos -> "pendiente"
            R.id.radioButtonFinalizadosFiltroAvisos -> "finalizado"
            R.id.radioButtonCanceladosFiltroAvisos -> "cancelado"
            else -> null
        }
    }

    private fun convertirFechaParaApi(fecha: String): String? {
        if (fecha.isBlank()) return null

        return try {
            val formatoPantalla = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).apply {
                isLenient = false
            }

            val fechaConvertida = formatoPantalla.parse(fecha)
                ?: return null

            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(fechaConvertida)
        } catch (e: Exception) {
            null
        }
    }

    private fun iniciarBusqueda(filtros: FiltrosBusqueda) {
        busquedaActual = filtros
        paginaActual = 1
        hayMasAvisos = true
        cargandoAvisos = false

        avisoAdapter.actualizarAvisos(emptyList())
        recyclerViewResultadosBuscarAvisos.scrollToPosition(0)

        cargarAvisos()
    }

    private fun configurarPaginacion() {
        recyclerViewResultadosBuscarAvisos.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager =
                        recyclerView.layoutManager as? LinearLayoutManager
                            ?: return

                    val ultimoVisible =
                        layoutManager.findLastVisibleItemPosition()

                    if (dy > 0 &&
                        !cargandoAvisos &&
                        hayMasAvisos &&
                        ultimoVisible >= avisoAdapter.itemCount - 2
                    ) {
                        cargarAvisos()
                    }
                }
            }
        )
    }

    private fun cargarAvisos() {
        if (cargandoAvisos || !hayMasAvisos) return

        val filtros = busquedaActual ?: FiltrosBusqueda()

        cargandoAvisos = true
        progressBarBuscarAvisos.visibility = View.VISIBLE

        RetrofitClient.api.obtenerAvisos(
            pagina = paginaActual,
            estado = filtros.estado,
            direccion = filtros.direccion,
            telefono = filtros.telefono,
            avisoId = filtros.avisoId,
            fecha = filtros.fecha,
            hora = filtros.hora,
            buscar = filtros.buscar
        ).enqueue(object : Callback<AvisosResponse> {

            override fun onResponse(
                call: Call<AvisosResponse>,
                response: Response<AvisosResponse>
            ) {
                progressBarBuscarAvisos.visibility = View.GONE
                cargandoAvisos = false

                if (!response.isSuccessful) {
                    Toast.makeText(
                        this@BuscarAvisosActivity,
                        "Error al buscar avisos: HTTP ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                val respuesta = response.body()

                if (respuesta == null || !respuesta.status) {
                    Toast.makeText(
                        this@BuscarAvisosActivity,
                        "No se pudieron obtener los avisos",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                if (paginaActual == 1) {
                    avisoAdapter.actualizarAvisos(respuesta.data)

                    if (respuesta.data.isEmpty()) {
                        Toast.makeText(
                            this@BuscarAvisosActivity,
                            "No se encontraron avisos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    avisoAdapter.agregarAvisos(respuesta.data)
                }

                hayMasAvisos =
                    respuesta.meta.current_page < respuesta.meta.last_page

                paginaActual = respuesta.meta.current_page + 1
            }

            override fun onFailure(
                call: Call<AvisosResponse>,
                t: Throwable
            ) {
                progressBarBuscarAvisos.visibility = View.GONE
                cargandoAvisos = false

                Toast.makeText(
                    this@BuscarAvisosActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }


    private fun abrirAvisoSeleccionado(aviso: Aviso) {
        val modoOperario =
            intent.getStringExtra("MODO_BUSQUEDA") == "OPERARIO"

        // Los avisos finalizados se abren en modo de solo lectura
        if (aviso.estado == "finalizado") {
            if (aviso.trabajo == null) {
                Toast.makeText(
                    this,
                    "No se encontraron los detalles del trabajo finalizado",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            val intentTrabajo = Intent(this, TrabajoActivity::class.java)

            intentTrabajo.putExtra("AVISO_ID", aviso.id)
            intentTrabajo.putExtra("AVISO_FECHA", aviso.fecha)
            intentTrabajo.putExtra("AVISO_HORA", aviso.hora)
            intentTrabajo.putExtra("AVISO_DIRECCION", aviso.direccion)
            intentTrabajo.putExtra("AVISO_TELEFONO", aviso.telefono)
            intentTrabajo.putExtra("AVISO_MENSAJE", aviso.mensaje)
            intentTrabajo.putExtra("AVISO_OBSERVACION", aviso.observacion)
            intentTrabajo.putExtra("AVISO_URGENCIA", aviso.urgencia)
            intentTrabajo.putExtra("AVISO_CLIENTE", aviso.cliente.nombre)
            intentTrabajo.putExtra("AVISO_FINALIZADO", true)
            intentTrabajo.putExtra(
                "AVISO_TRABAJO_JSON",
                Gson().toJson(aviso.trabajo)
            )

            startActivity(intentTrabajo)
            return
        }

        // Si es operario, abre el trabajo pendiente, no la pantalla de edición del administrador
        if (modoOperario) {
            val intentTrabajo = Intent(this, TrabajoActivity::class.java)

            intentTrabajo.putExtra("AVISO_ID", aviso.id)
            intentTrabajo.putExtra("AVISO_FECHA", aviso.fecha)
            intentTrabajo.putExtra("AVISO_HORA", aviso.hora)
            intentTrabajo.putExtra("AVISO_DIRECCION", aviso.direccion)
            intentTrabajo.putExtra("AVISO_TELEFONO", aviso.telefono)
            intentTrabajo.putExtra("AVISO_MENSAJE", aviso.mensaje)
            intentTrabajo.putExtra("AVISO_OBSERVACION", aviso.observacion)
            intentTrabajo.putExtra("AVISO_URGENCIA", aviso.urgencia)
            intentTrabajo.putExtra("AVISO_CLIENTE", aviso.cliente.nombre)
            intentTrabajo.putExtra("AVISO_FINALIZADO", false)

            startActivity(intentTrabajo)
            return
        }

        // Si es administrador, conserva la edición del aviso pendiente
        val intentEdicion = Intent(this, EditarAvisoActivity::class.java)
        intentEdicion.putExtra("AVISO_ID", aviso.id)
        startActivity(intentEdicion)
    }


    private fun limpiarCamposBusqueda() {
        editTextBusquedaGeneralAvisos.text.clear()
        editTextDireccionFiltroAvisos.text.clear()
        editTextTelefonoFiltroAvisos.text.clear()
        editTextNumeroAvisoFiltro.text.clear()
        editTextFechaFiltroAvisos.text.clear()
        editTextHoraFiltroAvisos.text.clear()

        radioGroupEstadoFiltroAvisos.check(
            R.id.radioButtonTodosEstadosAvisos
        )
    }

    private fun configurarSelectorFecha() {
        editTextFechaFiltroAvisos.setOnClickListener {
            val calendario = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, anio, mes, dia ->
                    val fechaSeleccionada = Calendar.getInstance().apply {
                        set(anio, mes, dia)
                    }

                    val formatoFecha = SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    )

                    editTextFechaFiltroAvisos.setText(
                        formatoFecha.format(fechaSeleccionada.time)
                    )
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun configurarSelectorHora() {
        editTextHoraFiltroAvisos.setOnClickListener {
            val calendario = Calendar.getInstance()

            TimePickerDialog(
                this,
                { _, horaSeleccionada, minutoSeleccionado ->
                    val horaFormateada = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        horaSeleccionada,
                        minutoSeleccionado
                    )

                    editTextHoraFiltroAvisos.setText(horaFormateada)
                },
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE),
                true
            ).show()
        }
    }
}
