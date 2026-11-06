package com.ivanbm.frontend_btz

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ivanbm.frontend_btz.model.AvisoRequest
import com.ivanbm.frontend_btz.model.AvisoResponse
import com.ivanbm.frontend_btz.model.Cliente
import com.ivanbm.frontend_btz.model.ClientesResponse
import com.ivanbm.frontend_btz.model.Usuario
import com.ivanbm.frontend_btz.model.UsuariosResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.AutoCompleteTextView
import android.app.DatePickerDialog
import java.util.Calendar
import android.app.TimePickerDialog
import android.widget.ImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Rect
import android.view.ViewTreeObserver
import org.json.JSONObject

class CrearAvisoActivity : AppCompatActivity() {

    private lateinit var editTextFechaAviso: EditText
    private lateinit var editTextHoraAviso: EditText
    private lateinit var editTextDireccionAviso: EditText
    private lateinit var editTextTelefonoAviso: EditText
    private lateinit var editTextMensajeAviso: EditText
    private lateinit var editTextObservacionAviso: EditText

    private lateinit var spinnerUrgenciaAviso: Spinner
    private lateinit var autoCompleteClienteAviso: AutoCompleteTextView
    private lateinit var autoCompleteOperarioAviso: AutoCompleteTextView
    private var clienteSeleccionado: Cliente? = null
    private var operarioSeleccionado: Usuario? = null
    private lateinit var buttonGuardarAviso: Button
    private lateinit var progressBarGuardarAviso: ProgressBar

    private lateinit var buttonAnadirClienteAviso: Button
    private var primeraCargaClientes = true
    private val clientes = mutableListOf<Cliente>()
    private val operarios = mutableListOf<Usuario>()

    private var cargandoClientes = false
    private var cargandoOperarios = false

    override fun onResume() {
        super.onResume()

        if (!primeraCargaClientes) {
            clientes.clear()
            cargarClientes()
        }

        primeraCargaClientes = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_aviso)

        val scrollViewCrearAviso =
            findViewById<android.widget.ScrollView>(
                R.id.scrollViewCrearAviso
            )

        val paddingInicialIzquierdo = scrollViewCrearAviso.paddingLeft
        val paddingInicialSuperior = scrollViewCrearAviso.paddingTop
        val paddingInicialDerecho = scrollViewCrearAviso.paddingRight
        val paddingInicialInferior = scrollViewCrearAviso.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(
            scrollViewCrearAviso
        ) { vista, insets ->

            val barrasSistema = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )

            val teclado = insets.getInsets(
                WindowInsetsCompat.Type.ime()
            )

            val tecladoVisible = insets.isVisible(
                WindowInsetsCompat.Type.ime()
            )

            vista.setPadding(
                paddingInicialIzquierdo + barrasSistema.left,
                paddingInicialSuperior + barrasSistema.top,
                paddingInicialDerecho + barrasSistema.right,
                paddingInicialInferior + if (tecladoVisible) {
                    maxOf(teclado.bottom, barrasSistema.bottom)
                } else {
                    barrasSistema.bottom
                }
            )

            insets
        }
        findViewById<ImageButton>(R.id.buttonVolverCrearAviso).setOnClickListener {
            finish()
        }

        editTextFechaAviso =
            findViewById(R.id.editTextFechaAviso)

        editTextFechaAviso.setOnClickListener {

            val calendario = Calendar.getInstance()

            val año = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val día = calendario.get(Calendar.DAY_OF_MONTH)

            val selectorFecha = DatePickerDialog(
                this,
                { _, añoSeleccionado, mesSeleccionado, díaSeleccionado ->

                    val fechaMostrada = String.format(
                        "%02d/%02d/%04d",
                        díaSeleccionado,
                        mesSeleccionado + 1,
                        añoSeleccionado
                    )

                    editTextFechaAviso.setText(fechaMostrada)
                },
                año,
                mes,
                día
            )

            selectorFecha.show()
        }

        editTextHoraAviso =
            findViewById(R.id.editTextHoraAviso)
        editTextHoraAviso.setOnClickListener {

            val calendario = Calendar.getInstance()

            val horaActual = calendario.get(Calendar.HOUR_OF_DAY)
            val minutoActual = calendario.get(Calendar.MINUTE)

            val selectorHora = TimePickerDialog(
                this,
                { _, horaSeleccionada, minutoSeleccionado ->

                    val horaMostrada = String.format(
                        "%02d:%02d",
                        horaSeleccionada,
                        minutoSeleccionado
                    )

                    editTextHoraAviso.setText(horaMostrada)
                },
                horaActual,
                minutoActual,
                true
            )

            selectorHora.show()
        }
        editTextDireccionAviso =
            findViewById(R.id.editTextDireccionAviso)

        editTextTelefonoAviso =
            findViewById(R.id.editTextTelefonoAviso)

        editTextMensajeAviso =
            findViewById(R.id.editTextMensajeAviso)

        editTextObservacionAviso =
            findViewById(R.id.editTextObservacionAviso)

        editTextMensajeAviso.filters = arrayOf(
            android.text.InputFilter.LengthFilter(255)
        )

        editTextObservacionAviso.filters = arrayOf(
            android.text.InputFilter.LengthFilter(255)
        )

        spinnerUrgenciaAviso =
            findViewById(R.id.spinnerUrgenciaAviso)

        autoCompleteClienteAviso =
            findViewById(R.id.autoCompleteClienteAviso)

        autoCompleteOperarioAviso =
            findViewById(R.id.autoCompleteOperarioAviso)

        buttonGuardarAviso =
            findViewById(R.id.buttonGuardarAviso)

        progressBarGuardarAviso =
            findViewById(R.id.progressBarGuardarAviso)

        buttonAnadirClienteAviso =
            findViewById(R.id.buttonAnadirClienteAviso)

        scrollViewCrearAviso.viewTreeObserver.addOnGlobalLayoutListener {
            val rectanguloVisible = Rect()
            scrollViewCrearAviso.getWindowVisibleDisplayFrame(rectanguloVisible)

            val campoEnfocado = currentFocus ?: return@addOnGlobalLayoutListener

            if (campoEnfocado !is EditText) {
                return@addOnGlobalLayoutListener
            }

            if (!campoEnfocado.isShown) {
                return@addOnGlobalLayoutListener
            }

            val posicionCampo = Rect()
            campoEnfocado.getDrawingRect(posicionCampo)
            scrollViewCrearAviso.offsetDescendantRectToMyCoords(
                campoEnfocado,
                posicionCampo
            )

            val margen = (16 * resources.displayMetrics.density).toInt()
            val parteSuperiorVisible =
                scrollViewCrearAviso.scrollY + margen

            val parteInferiorVisible =
                scrollViewCrearAviso.scrollY +
                        scrollViewCrearAviso.height -
                        scrollViewCrearAviso.paddingBottom -
                        margen

            when {
                posicionCampo.bottom > parteInferiorVisible -> {
                    scrollViewCrearAviso.smoothScrollBy(
                        0,
                        posicionCampo.bottom - parteInferiorVisible
                    )
                }

                posicionCampo.top < parteSuperiorVisible -> {
                    scrollViewCrearAviso.smoothScrollBy(
                        0,
                        posicionCampo.top - parteSuperiorVisible
                    )
                }
            }
        }

        buttonGuardarAviso.setOnClickListener {
            crearAviso()
        }
        buttonAnadirClienteAviso.setOnClickListener {
            val intent = android.content.Intent(
                this,
                CrearClienteActivity::class.java
            )

            startActivity(intent)
        }

        configurarSpinnerUrgencia()
        cargarClientes()
        cargarOperarios()
    }

    private fun configurarSpinnerUrgencia() {
        val urgencias = listOf("Urgente", "Media", "Baja")

        fun colorUrgencia(urgencia: String): Int {
            return when (urgencia) {
                "Urgente" -> android.graphics.Color.rgb(198, 40, 40)
                "Media" -> android.graphics.Color.rgb(239, 143, 0)
                else -> android.graphics.Color.rgb(46, 125, 50)
            }
        }

        fun fondoUrgencia(urgencia: String): Int {
            return when (urgencia) {
                "Urgente" -> R.drawable.bg_urgencia_urgente
                "Media" -> R.drawable.bg_urgencia_media
                else -> R.drawable.bg_urgencia_baja
            }
        }

        val adapter = object : ArrayAdapter<String>(
            this,
            R.layout.item_urgencia_dropdown,
            R.id.textViewOpcionUrgencia,
            urgencias
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: android.view.ViewGroup
            ): View {
                val view = super.getView(position, convertView, parent)
                val urgencia = getItem(position) ?: ""

                val texto = view.findViewById<android.widget.TextView>(
                    R.id.textViewOpcionUrgencia
                )

                texto.apply {
                    text = urgencia
                    setTextColor(colorUrgencia(urgencia))
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }

                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: android.view.ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val urgencia = getItem(position) ?: ""

                view.findViewById<android.widget.TextView>(
                    R.id.textViewOpcionUrgencia
                ).apply {
                    text = urgencia
                    setTextColor(colorUrgencia(urgencia))
                    setBackgroundResource(fondoUrgencia(urgencia))
                }

                return view
            }
        }

        spinnerUrgenciaAviso.adapter = adapter

        fun actualizarFondoUrgencia(urgencia: String) {
            val colorFondo = when (urgencia) {
                "Urgente" -> android.graphics.Color.rgb(252, 232, 230)
                "Media" -> android.graphics.Color.rgb(255, 242, 214)
                else -> android.graphics.Color.rgb(227, 245, 232)
            }

            spinnerUrgenciaAviso.backgroundTintList =
                android.content.res.ColorStateList.valueOf(colorFondo)
        }

        spinnerUrgenciaAviso.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    actualizarFondoUrgencia(urgencias[position])
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) = Unit
            }

        actualizarFondoUrgencia(
            spinnerUrgenciaAviso.selectedItem.toString()
        )
    }
    private fun cargarClientes(pagina: Int = 1) {

        if (pagina == 1) {
            clientes.clear()
        }

        cargandoClientes = true

        RetrofitClient.api.obtenerClientes(pagina)
            .enqueue(object : Callback<ClientesResponse> {

                override fun onResponse(
                    call: Call<ClientesResponse>,
                    response: Response<ClientesResponse>
                ) {

                    if (!response.isSuccessful) {

                        cargandoClientes = false

                        Toast.makeText(
                            this@CrearAvisoActivity,
                            "Error al cargar clientes: HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    val respuesta = response.body()

                    if (respuesta != null) {

                        clientes.addAll(respuesta.data)

                        if (
                            respuesta.meta.current_page <
                            respuesta.meta.last_page
                        ) {

                            cargarClientes(
                                respuesta.meta.current_page + 1
                            )

                        } else {

                            cargandoClientes = false
                            configurarClientes()
                        }

                    } else {

                        cargandoClientes = false

                        Toast.makeText(
                            this@CrearAvisoActivity,
                            "No se pudieron cargar los clientes",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ClientesResponse>,
                    t: Throwable
                ) {

                    cargandoClientes = false

                    Toast.makeText(
                        this@CrearAvisoActivity,
                        "Error de conexión al cargar clientes: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun configurarClientes() {

        val adapter = object : ArrayAdapter<Cliente>(
            this,
            R.layout.item_opcion_aviso,
            R.id.textViewNombreOpcionAviso,
            clientes
        ) {

            private val clientesOriginales = ArrayList(clientes)

            override fun getView(
                position: Int,
                convertView: View?,
                parent: android.view.ViewGroup
            ): View {

                val view = super.getView(position, convertView, parent)

                view.findViewById<android.widget.TextView>(
                    R.id.textViewNombreOpcionAviso
                ).text = getItem(position)?.nombre ?: ""

                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: android.view.ViewGroup
            ): View {

                val view = super.getDropDownView(
                    position,
                    convertView,
                    parent
                )

                view.findViewById<android.widget.TextView>(
                    R.id.textViewNombreOpcionAviso
                ).text = getItem(position)?.nombre ?: ""

                return view
            }

            override fun getFilter(): android.widget.Filter {

                return object : android.widget.Filter() {

                    override fun performFiltering(
                        constraint: CharSequence?
                    ): FilterResults {

                        val resultados = FilterResults()

                        val textoBuscado =
                            constraint?.toString()?.trim()?.lowercase()
                                ?: ""

                        val listaFiltrada =
                            if (textoBuscado.isEmpty()) {

                                clientesOriginales

                            } else {

                                clientesOriginales.filter { cliente ->

                                    cliente.nombre
                                        .lowercase()
                                        .contains(textoBuscado)

                                }
                            }

                        resultados.values = listaFiltrada
                        resultados.count = listaFiltrada.size

                        return resultados
                    }

                    override fun publishResults(
                        constraint: CharSequence?,
                        results: FilterResults?
                    ) {

                        clear()

                        if (results?.values is List<*>) {

                            @Suppress("UNCHECKED_CAST")
                            addAll(
                                results.values as List<Cliente>
                            )
                        }

                        notifyDataSetChanged()
                    }
                }
            }
        }

        autoCompleteClienteAviso.setAdapter(adapter)

        autoCompleteClienteAviso.threshold = 1

        autoCompleteClienteAviso.setOnItemClickListener {
                parent, _, position, _ ->

            clienteSeleccionado =
                parent.getItemAtPosition(position) as Cliente

            autoCompleteClienteAviso.setText(
                clienteSeleccionado?.nombre,
                false
            )
        }
    }

    private fun cargarOperarios(pagina: Int = 1) {

        cargandoOperarios = true

        RetrofitClient.api.obtenerUsuarios(pagina)
            .enqueue(object : Callback<UsuariosResponse> {

                override fun onResponse(
                    call: Call<UsuariosResponse>,
                    response: Response<UsuariosResponse>
                ) {

                    if (!response.isSuccessful) {

                        cargandoOperarios = false

                        Toast.makeText(
                            this@CrearAvisoActivity,
                            "Error al cargar usuarios: HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    val respuesta = response.body()

                    if (respuesta != null) {

                        operarios.addAll(
                            respuesta.data.filter { usuario ->
                                usuario.rol == "operario"
                            }
                        )

                        if (
                            respuesta.meta != null &&
                            respuesta.meta.current_page < respuesta.meta.last_page
                        ) {

                            cargarOperarios(
                                respuesta.meta.current_page + 1
                            )

                        } else {

                            cargandoOperarios = false
                            configurarOperarios()
                        }

                    } else {

                        cargandoOperarios = false

                        Toast.makeText(
                            this@CrearAvisoActivity,
                            "No se pudieron cargar los usuarios",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<UsuariosResponse>,
                    t: Throwable
                ) {

                    cargandoOperarios = false

                    Toast.makeText(
                        this@CrearAvisoActivity,
                        "Error de conexión al cargar usuarios: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun configurarOperarios() {

        val adapter = object : ArrayAdapter<Usuario>(
            this,
            R.layout.item_opcion_aviso,
            R.id.textViewNombreOpcionAviso,
            operarios
        ) {
            private val operariosOriginales = ArrayList(operarios)

            override fun getView(
                position: Int,
                convertView: View?,
                parent: android.view.ViewGroup
            ): View {

                val view = super.getView(position, convertView, parent)

                view.findViewById<android.widget.TextView>(
                    R.id.textViewNombreOpcionAviso
                ).text = getItem(position)?.nombre ?: ""

                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: android.view.ViewGroup
            ): View {

                val view = super.getDropDownView(
                    position,
                    convertView,
                    parent
                )

                view.findViewById<android.widget.TextView>(
                    R.id.textViewNombreOpcionAviso
                ).text = getItem(position)?.nombre ?: ""

                return view
            }

            override fun getFilter(): android.widget.Filter {

                return object : android.widget.Filter() {

                    override fun performFiltering(
                        constraint: CharSequence?
                    ): FilterResults {

                        val resultados = FilterResults()

                        val textoBuscado =
                            constraint?.toString()?.trim()?.lowercase()
                                ?: ""

                        val listaFiltrada =
                            if (textoBuscado.isEmpty()) {

                                operariosOriginales

                            } else {

                                operariosOriginales.filter { operario ->

                                    operario.nombre
                                        .lowercase()
                                        .contains(textoBuscado)

                                }
                            }

                        resultados.values = listaFiltrada
                        resultados.count = listaFiltrada.size

                        return resultados
                    }

                    override fun publishResults(
                        constraint: CharSequence?,
                        results: FilterResults?
                    ) {

                        clear()

                        if (results?.values is List<*>) {

                            @Suppress("UNCHECKED_CAST")
                            addAll(
                                results.values as List<Usuario>
                            )
                        }

                        notifyDataSetChanged()
                    }
                }
            }
        }

        autoCompleteOperarioAviso.setAdapter(adapter)

        autoCompleteOperarioAviso.threshold = 1

        autoCompleteOperarioAviso.setOnItemClickListener {
                parent, _, position, _ ->

            operarioSeleccionado =
                parent.getItemAtPosition(position) as Usuario

            autoCompleteOperarioAviso.setText(
                operarioSeleccionado?.nombre,
                false
            )
        }
    }

    private fun crearAviso() {

        val fechaIngresada = editTextFechaAviso.text.toString()

        val partesFecha = fechaIngresada.split("/")

        val fechaParaBackend =
            "${partesFecha[2]}-${partesFecha[1]}-${partesFecha[0]}"

        val fecha = fechaParaBackend

        val hora =
            editTextHoraAviso.text.toString().trim()

        val direccion =
            editTextDireccionAviso.text.toString().trim()

        val telefono =
            editTextTelefonoAviso.text.toString().trim()

        val mensaje =
            editTextMensajeAviso.text.toString().trim()

        val observacion =
            editTextObservacionAviso.text.toString().trim()

        if (mensaje.length > 255) {
            editTextMensajeAviso.error =
                "El mensaje no puede superar los 255 caracteres"
            editTextMensajeAviso.requestFocus()
            return
        }

        if (observacion.length > 255) {
            editTextObservacionAviso.error =
                "La observación no puede superar los 255 caracteres"
            editTextObservacionAviso.requestFocus()
            return
        }

        if (fecha.isEmpty()) {

            editTextFechaAviso.error =
                "Ingrese la fecha"

            editTextFechaAviso.requestFocus()
            return
        }

        if (hora.isEmpty()) {

            editTextHoraAviso.error =
                "Ingrese la hora"

            editTextHoraAviso.requestFocus()
            return
        }

        if (direccion.isEmpty()) {

            editTextDireccionAviso.error =
                "Ingrese la dirección"

            editTextDireccionAviso.requestFocus()
            return
        }

        if (telefono.isEmpty()) {

            editTextTelefonoAviso.error =
                "Ingrese el teléfono"

            editTextTelefonoAviso.requestFocus()
            return
        }

        if (clientes.isEmpty()) {

            Toast.makeText(
                this,
                "No hay clientes disponibles",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (operarios.isEmpty()) {

            Toast.makeText(
                this,
                "No hay operarios disponibles",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (clienteSeleccionado == null) {

            autoCompleteClienteAviso.error =
                "Seleccione un cliente"

            autoCompleteClienteAviso.requestFocus()
            return
        }

        if (operarioSeleccionado == null) {

            autoCompleteOperarioAviso.error =
                "Seleccione un operario"

            autoCompleteOperarioAviso.requestFocus()
            return
        }

        val urgenciaSeleccionada =
            spinnerUrgenciaAviso.selectedItem
                .toString()
                .lowercase()

        val request = AvisoRequest(
            fecha = fecha,
            hora = hora,
            direccion = direccion,
            telefono = telefono,
            mensaje =
                if (mensaje.isEmpty()) null else mensaje,
            observacion =
                if (observacion.isEmpty()) null else observacion,
            estado = "pendiente",
            urgencia = urgenciaSeleccionada,
            usuario_id = operarioSeleccionado!!.id,
            cliente_id = clienteSeleccionado!!.id
        )

        mostrarCargando(true)

        RetrofitClient.api.crearAviso(request)
            .enqueue(object : Callback<AvisoResponse> {

                override fun onResponse(
                    call: Call<AvisoResponse>,
                    response: Response<AvisoResponse>
                ) {

                    mostrarCargando(false)

                    if (!response.isSuccessful) {
                        val error = response.errorBody()?.string()

                        android.util.Log.e(
                            "CREAR_AVISO",
                            "Código HTTP: ${response.code()}"
                        )

                        android.util.Log.e(
                            "CREAR_AVISO",
                            error ?: "Sin información del error"
                        )

                        mostrarErrorServidor(
                            response.code(),
                            error
                        )

                        return
                    }
                    val respuesta = response.body()

                    android.util.Log.d(
                        "CREAR_AVISO",
                        "Respuesta: $respuesta"
                    )

                    if (respuesta != null && respuesta.status) {

                        Toast.makeText(
                            this@CrearAvisoActivity,
                            respuesta.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this@CrearAvisoActivity,
                            respuesta?.message
                                ?: "No se pudo crear el aviso",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<AvisoResponse>,
                    t: Throwable
                ) {

                    mostrarCargando(false)

                    android.util.Log.e(
                        "CREAR_AVISO",
                        "Error de conexión",
                        t
                    )

                    Toast.makeText(
                        this@CrearAvisoActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
    private fun mostrarErrorServidor(
        codigoHttp: Int,
        cuerpoError: String?
    ) {
        if (codigoHttp == 422 && !cuerpoError.isNullOrBlank()) {
            try {
                val json = JSONObject(cuerpoError)
                val errores = json.optJSONObject("errors")

                val mensajeError = errores
                    ?.optJSONArray("mensaje")
                    ?.optString(0)
                    ?.takeIf { it.isNotBlank() }

                val observacionError = errores
                    ?.optJSONArray("observacion")
                    ?.optString(0)
                    ?.takeIf { it.isNotBlank() }

                if (mensajeError != null) {
                    editTextMensajeAviso.error =
                        "El mensaje no puede superar los 255 caracteres"
                    editTextMensajeAviso.requestFocus()
                }

                if (observacionError != null) {
                    editTextObservacionAviso.error =
                        "La observación no puede superar los 255 caracteres"

                    if (mensajeError == null) {
                        editTextObservacionAviso.requestFocus()
                    }
                }

                if (mensajeError != null || observacionError != null) {
                    Toast.makeText(
                        this,
                        "Revisá los campos indicados: máximo 255 caracteres",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                Toast.makeText(
                    this,
                    "Hay datos inválidos. Revisá los campos ingresados.",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Los datos ingresados no son válidos.",
                    Toast.LENGTH_LONG
                ).show()
            }

            return
        }

        Toast.makeText(
            this,
            "No se pudo crear el aviso. Error del servidor: HTTP $codigoHttp",
            Toast.LENGTH_LONG
        ).show()
    }
    private fun mostrarCargando(cargando: Boolean) {

        if (cargando) {

            progressBarGuardarAviso.visibility =
                ProgressBar.VISIBLE

            buttonGuardarAviso.isEnabled = false

        } else {

            progressBarGuardarAviso.visibility =
                ProgressBar.GONE

            buttonGuardarAviso.isEnabled = true
        }
    }
}