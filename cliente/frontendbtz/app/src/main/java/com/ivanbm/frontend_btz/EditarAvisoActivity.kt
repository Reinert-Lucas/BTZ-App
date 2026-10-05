package com.ivanbm.frontend_btz

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ivanbm.frontend_btz.model.Aviso
import com.ivanbm.frontend_btz.model.AvisoRequest
import com.ivanbm.frontend_btz.model.AvisoResponse
import com.ivanbm.frontend_btz.model.Cliente
import com.ivanbm.frontend_btz.model.ClientesResponse
import com.ivanbm.frontend_btz.model.Usuario
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditarAvisoActivity : AppCompatActivity() {

    private lateinit var editTextFechaEditarAviso: EditText
    private lateinit var editTextHoraEditarAviso: EditText
    private lateinit var editTextDireccionEditarAviso: EditText
    private lateinit var editTextTelefonoEditarAviso: EditText
    private lateinit var editTextMensajeEditarAviso: EditText
    private lateinit var editTextObservacionEditarAviso: EditText

    private lateinit var spinnerUrgenciaEditarAviso: Spinner

    private lateinit var autoCompleteClienteEditarAviso: AutoCompleteTextView
    private lateinit var autoCompleteOperarioEditarAviso: AutoCompleteTextView

    private lateinit var buttonAnadirClienteEditarAviso: Button
    private lateinit var buttonGuardarCambiosAviso: Button
    private lateinit var buttonEliminarAviso: Button

    private lateinit var progressBarEditarAviso: ProgressBar

    private val clientes = mutableListOf<Cliente>()
    private val operarios = mutableListOf<Usuario>()

    private var clienteSeleccionado: Cliente? = null
    private var operarioSeleccionado: Usuario? = null

    private var avisoId: Int = -1

    private var estadoAviso: String = "pendiente"

    private var cargandoClientes = false
    private var cargandoOperarios = false

    private var avisoCargado = false

    private var clienteIdAviso: Int = -1
    private var operarioIdAviso: Int = -1

    private var primeraCargaClientes = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_aviso)

        inicializarVistas()

        avisoId = intent.getIntExtra("AVISO_ID", -1)

        if (avisoId == -1) {
            Toast.makeText(
                this,
                "No se encontró el aviso",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        configurarSelectorFecha()
        configurarSelectorHora()
        configurarSpinnerUrgencia()
        configurarBotonAnadirCliente()

        buttonGuardarCambiosAviso.setOnClickListener {
            actualizarAviso()
        }

        buttonEliminarAviso.setOnClickListener {
            mostrarConfirmacionEliminar()
        }

        cargarAviso()
        cargarClientes()
        cargarOperarios()
    }

    private fun inicializarVistas() {

        editTextFechaEditarAviso =
            findViewById(R.id.editTextFechaEditarAviso)

        editTextHoraEditarAviso =
            findViewById(R.id.editTextHoraEditarAviso)

        editTextDireccionEditarAviso =
            findViewById(R.id.editTextDireccionEditarAviso)

        editTextTelefonoEditarAviso =
            findViewById(R.id.editTextTelefonoEditarAviso)

        editTextMensajeEditarAviso =
            findViewById(R.id.editTextMensajeEditarAviso)

        editTextObservacionEditarAviso =
            findViewById(R.id.editTextObservacionEditarAviso)

        spinnerUrgenciaEditarAviso =
            findViewById(R.id.spinnerUrgenciaEditarAviso)

        autoCompleteClienteEditarAviso =
            findViewById(R.id.autoCompleteClienteEditarAviso)

        autoCompleteOperarioEditarAviso =
            findViewById(R.id.autoCompleteOperarioEditarAviso)

        buttonAnadirClienteEditarAviso =
            findViewById(R.id.buttonAnadirClienteEditarAviso)

        buttonGuardarCambiosAviso =
            findViewById(R.id.buttonGuardarCambiosAviso)

        buttonEliminarAviso =
            findViewById(R.id.buttonEliminarAviso)

        progressBarEditarAviso =
            findViewById(R.id.progressBarEditarAviso)
    }

    private fun configurarSelectorFecha() {

        editTextFechaEditarAviso.setOnClickListener {

            val calendario = Calendar.getInstance()

            val fechaActual = editTextFechaEditarAviso.text.toString()

            if (fechaActual.isNotEmpty()) {

                try {

                    val formato =
                        SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        )

                    val fecha =
                        formato.parse(fechaActual)

                    if (fecha != null) {
                        calendario.time = fecha
                    }

                } catch (_: Exception) {
                }
            }

            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->

                    val fechaSeleccionada =
                        String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            dayOfMonth,
                            month + 1,
                            year
                        )

                    editTextFechaEditarAviso.setText(
                        fechaSeleccionada
                    )
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun configurarSelectorHora() {

        editTextHoraEditarAviso.setOnClickListener {

            val calendario = Calendar.getInstance()

            val horaActual =
                editTextHoraEditarAviso.text.toString()

            var hora = calendario.get(Calendar.HOUR_OF_DAY)
            var minuto = calendario.get(Calendar.MINUTE)

            if (horaActual.isNotEmpty()) {

                try {

                    val partes = horaActual.split(":")

                    if (partes.size == 2) {
                        hora = partes[0].toInt()
                        minuto = partes[1].toInt()
                    }

                } catch (_: Exception) {
                }
            }

            TimePickerDialog(
                this,
                { _, horaSeleccionada, minutoSeleccionado ->

                    val horaFormateada =
                        String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            horaSeleccionada,
                            minutoSeleccionado
                        )

                    editTextHoraEditarAviso.setText(
                        horaFormateada
                    )
                },
                hora,
                minuto,
                true
            ).show()
        }
    }

    private fun configurarSpinnerUrgencia() {

        val urgencias =
            listOf(
                "Urgente",
                "Media",
                "Baja"
            )

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                urgencias
            )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerUrgenciaEditarAviso.adapter = adapter
    }


    private fun cargarAviso() {

        mostrarCargando(true)

        RetrofitClient.api
            .obtenerAviso(avisoId)
            .enqueue(object : Callback<AvisoResponse> {

                override fun onResponse(
                    call: Call<AvisoResponse>,
                    response: Response<AvisoResponse>
                ) {

                    mostrarCargando(false)

                    if (response.isSuccessful && response.body() != null) {

                        val aviso =
                            response.body()!!.data

                        cargarDatosAviso(aviso)

                    } else {

                        Toast.makeText(
                            this@EditarAvisoActivity,
                            "No se pudo cargar el aviso",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }
                }

                override fun onFailure(
                    call: Call<AvisoResponse>,
                    t: Throwable
                ) {

                    mostrarCargando(false)

                    Log.e(
                        "EDITAR_AVISO",
                        "Error al cargar aviso",
                        t
                    )

                    Toast.makeText(
                        this@EditarAvisoActivity,
                        "Error de conexión",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            })
    }

    private fun cargarDatosAviso(aviso: Aviso) {

        avisoCargado = true

        estadoAviso = aviso.estado

        clienteIdAviso = aviso.cliente.id
        operarioIdAviso = aviso.operario.id

        try {

            val formatoBackend =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            val formatoPantalla =
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                )

            val fecha =
                formatoBackend.parse(aviso.fecha)

            if (fecha != null) {

                editTextFechaEditarAviso.setText(
                    formatoPantalla.format(fecha)
                )
            }

        } catch (e: Exception) {

            Log.e(
                "EDITAR_AVISO",
                "Error al convertir fecha",
                e
            )

            editTextFechaEditarAviso.setText(
                aviso.fecha
            )
        }

        editTextHoraEditarAviso.setText(
            aviso.hora
        )

        editTextDireccionEditarAviso.setText(
            aviso.direccion
        )

        editTextTelefonoEditarAviso.setText(
            aviso.telefono
        )

        editTextMensajeEditarAviso.setText(
            aviso.mensaje ?: ""
        )

        editTextObservacionEditarAviso.setText(
            aviso.observacion ?: ""
        )

        seleccionarUrgencia(
            aviso.urgencia
        )

        intentarSeleccionarAsignados()
    }

    private fun intentarSeleccionarAsignados() {

        if (!avisoCargado) {
            return
        }

        if (clientes.isNotEmpty()) {
            seleccionarClienteSiEstaCargado()
        }

        if (operarios.isNotEmpty()) {
            seleccionarOperarioSiEstaCargado()
        }
    }
    private fun cargarClientes(
        pagina: Int = 1
    ) {

        cargandoClientes = true

        RetrofitClient.api
            .obtenerClientes(pagina)
            .enqueue(object : Callback<ClientesResponse> {

                override fun onResponse(
                    call: Call<ClientesResponse>,
                    response: Response<ClientesResponse>
                ) {

                    if (!response.isSuccessful ||
                        response.body() == null
                    ) {

                        cargandoClientes = false

                        Toast.makeText(
                            this@EditarAvisoActivity,
                            "No se pudieron cargar los clientes",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    val respuesta =
                        response.body()!!

                    clientes.addAll(
                        respuesta.data
                    )

                    val meta =
                        respuesta.meta

                    if (
                        meta != null &&
                        meta.current_page < meta.last_page
                    ) {

                        cargarClientes(
                            meta.current_page + 1
                        )

                    } else {

                        cargandoClientes = false

                        configurarClientes()

                        intentarSeleccionarAsignados()
                    }
                }

                override fun onFailure(
                    call: Call<ClientesResponse>,
                    t: Throwable
                ) {

                    cargandoClientes = false

                    Log.e(
                        "EDITAR_AVISO",
                        "Error al cargar clientes",
                        t
                    )

                    Toast.makeText(
                        this@EditarAvisoActivity,
                        "Error al cargar clientes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun configurarClientes() {

        val adapter = object : ArrayAdapter<Cliente>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            clientes
        ) {

            private val clientesOriginales =
                ArrayList(clientes)

            override fun getView(
                position: Int,
                convertView: android.view.View?,
                parent: android.view.ViewGroup
            ): android.view.View {

                val view =
                    super.getView(
                        position,
                        convertView,
                        parent
                    )

                view.findViewById<TextView>(
                    android.R.id.text1
                ).text =
                    getItem(position)?.nombre ?: ""

                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: android.view.View?,
                parent: android.view.ViewGroup
            ): android.view.View {

                val view =
                    super.getDropDownView(
                        position,
                        convertView,
                        parent
                    )

                view.findViewById<TextView>(
                    android.R.id.text1
                ).text =
                    getItem(position)?.nombre ?: ""

                return view
            }

            override fun getFilter(): android.widget.Filter {

                return object : android.widget.Filter() {

                    override fun performFiltering(
                        constraint: CharSequence?
                    ): FilterResults {

                        val resultados =
                            FilterResults()

                        val textoBuscado =
                            constraint
                                ?.toString()
                                ?.trim()
                                ?.lowercase()
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

                        resultados.values =
                            listaFiltrada

                        resultados.count =
                            listaFiltrada.size

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

        autoCompleteClienteEditarAviso.setAdapter(
            adapter
        )

        autoCompleteClienteEditarAviso.threshold = 1

        autoCompleteClienteEditarAviso.setOnItemClickListener {
                parent, _, position, _ ->

            clienteSeleccionado =
                parent.getItemAtPosition(position) as Cliente

            autoCompleteClienteEditarAviso.setText(
                clienteSeleccionado?.nombre,
                false
            )

            Log.d(
                "EDITAR_AVISO",
                "Cliente seleccionado: ${clienteSeleccionado?.nombre} - ID: ${clienteSeleccionado?.id}"
            )
        }
    }

    private fun seleccionarClienteSiEstaCargado() {

        Log.d(
            "EDITAR_AVISO",
            "BUSCANDO CLIENTE -> avisoCargado=$avisoCargado | clienteIdAviso=$clienteIdAviso | cantidadClientes=${clientes.size}"
        )

        if (!avisoCargado) {
            return
        }

        if (clientes.isEmpty()) {
            return
        }

        val cliente =
            clientes.find {
                it.id == clienteIdAviso
            }

        if (cliente == null) {

            Log.e(
                "EDITAR_AVISO",
                "NO ENCONTRÉ EL CLIENTE. ID BUSCADO=$clienteIdAviso"
            )

            Log.e(
                "EDITAR_AVISO",
                "IDs DISPONIBLES=${clientes.map { it.id }}"
            )

            return
        }

        clienteSeleccionado = cliente

        autoCompleteClienteEditarAviso.setText(
            cliente.nombre,
            false
        )

        Log.d(
            "EDITAR_AVISO",
            "CLIENTE SELECCIONADO CORRECTAMENTE -> ${cliente.nombre} | ID=${cliente.id}"
        )
    }
    private fun cargarOperarios(
        pagina: Int = 1
    ) {

        cargandoOperarios = true

        RetrofitClient.api
            .obtenerUsuarios(pagina)
            .enqueue(object : Callback<com.ivanbm.frontend_btz.model.UsuariosResponse> {

                override fun onResponse(
                    call: Call<com.ivanbm.frontend_btz.model.UsuariosResponse>,
                    response: Response<com.ivanbm.frontend_btz.model.UsuariosResponse>
                ) {

                    if (!response.isSuccessful ||
                        response.body() == null
                    ) {

                        cargandoOperarios = false

                        Toast.makeText(
                            this@EditarAvisoActivity,
                            "No se pudieron cargar los operarios",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    val respuesta =
                        response.body()!!

                    Log.d(
                        "EDITAR_AVISO",
                        "PÁGINA USUARIOS -> página=$pagina | usuarios=${respuesta.data.map { "${it.id}:${it.nombre}:${it.rol}" }}"
                    )

                    val operariosPagina =
                        respuesta.data.filter {
                            it.rol == "operario"
                        }

                    Log.d(
                        "EDITAR_AVISO",
                        "OPERARIOS DE ESTA PÁGINA -> ${operariosPagina.map { "${it.id}:${it.nombre}" }}"
                    )

                    operarios.addAll(
                        operariosPagina
                    )

                    val meta =
                        respuesta.meta

                    if (
                        meta != null &&
                        meta.current_page < meta.last_page
                    ) {
                        Log.d(
                            "EDITAR_AVISO",
                            "CARGANDO SIGUIENTE PÁGINA DE USUARIOS -> ${meta.current_page + 1} de ${meta.last_page}"
                        )
                        cargarOperarios(
                            meta.current_page + 1
                        )

                    } else {

                        cargandoOperarios = false

                        configurarOperarios()

                        intentarSeleccionarAsignados()
                    }
                }

                override fun onFailure(
                    call: Call<com.ivanbm.frontend_btz.model.UsuariosResponse>,
                    t: Throwable
                ) {

                    cargandoOperarios = false

                    Log.e(
                        "EDITAR_AVISO",
                        "Error al cargar operarios",
                        t
                    )

                    Toast.makeText(
                        this@EditarAvisoActivity,
                        "Error al cargar operarios",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun configurarOperarios() {

        val adapter = object : ArrayAdapter<Usuario>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            operarios
        ) {

            private val operariosOriginales =
                ArrayList(operarios)

            override fun getView(
                position: Int,
                convertView: android.view.View?,
                parent: android.view.ViewGroup
            ): android.view.View {

                val view =
                    super.getView(
                        position,
                        convertView,
                        parent
                    )

                view.findViewById<TextView>(
                    android.R.id.text1
                ).text =
                    getItem(position)?.nombre ?: ""

                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: android.view.View?,
                parent: android.view.ViewGroup
            ): android.view.View {

                val view =
                    super.getDropDownView(
                        position,
                        convertView,
                        parent
                    )

                view.findViewById<TextView>(
                    android.R.id.text1
                ).text =
                    getItem(position)?.nombre ?: ""

                return view
            }

            override fun getFilter(): android.widget.Filter {

                return object : android.widget.Filter() {

                    override fun performFiltering(
                        constraint: CharSequence?
                    ): FilterResults {

                        val resultados =
                            FilterResults()

                        val textoBuscado =
                            constraint
                                ?.toString()
                                ?.trim()
                                ?.lowercase()
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

                        resultados.values =
                            listaFiltrada

                        resultados.count =
                            listaFiltrada.size

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

        autoCompleteOperarioEditarAviso.setAdapter(
            adapter
        )

        autoCompleteOperarioEditarAviso.threshold = 1

        autoCompleteOperarioEditarAviso.setOnItemClickListener {
                parent, _, position, _ ->

            operarioSeleccionado =
                parent.getItemAtPosition(position) as Usuario

            autoCompleteOperarioEditarAviso.setText(
                operarioSeleccionado?.nombre,
                false
            )

            Log.d(
                "EDITAR_AVISO",
                "Operario seleccionado: ${operarioSeleccionado?.nombre} - ID: ${operarioSeleccionado?.id}"
            )
        }
    }

    private fun seleccionarOperarioSiEstaCargado() {

        Log.d(
            "EDITAR_AVISO",
            "BUSCANDO OPERARIO -> avisoCargado=$avisoCargado | operarioIdAviso=$operarioIdAviso | cantidadOperarios=${operarios.size}"
        )

        if (!avisoCargado) {
            return
        }

        if (operarios.isEmpty()) {
            return
        }

        val operario =
            operarios.find {
                it.id == operarioIdAviso
            }

        if (operario == null) {

            Log.e(
                "EDITAR_AVISO",
                "NO ENCONTRÉ EL OPERARIO. ID BUSCADO=$operarioIdAviso"
            )

            Log.e(
                "EDITAR_AVISO",
                "IDs DISPONIBLES=${operarios.map { it.id }}"
            )

            return
        }

        operarioSeleccionado = operario

        autoCompleteOperarioEditarAviso.setText(
            operario.nombre,
            false
        )

        Log.d(
            "EDITAR_AVISO",
            "OPERARIO SELECCIONADO CORRECTAMENTE -> ${operario.nombre} | ID=${operario.id}"
        )
    }

    private fun seleccionarUrgencia(
        urgencia: String
    ) {

        val urgencias =
            listOf(
                "Urgente",
                "Media",
                "Baja"
            )

        val posicion =
            urgencias.indexOfFirst {
                it.equals(
                    urgencia,
                    ignoreCase = true
                )
            }

        if (posicion >= 0) {

            spinnerUrgenciaEditarAviso.setSelection(
                posicion
            )
        }
    }

    private fun configurarBotonAnadirCliente() {

        buttonAnadirClienteEditarAviso.setOnClickListener {

            val intent =
                Intent(
                    this,
                    CrearClienteActivity::class.java
                )

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        if (!primeraCargaClientes) {

            clientes.clear()
            clienteSeleccionado = null

            cargarClientes()
        }

        primeraCargaClientes = false
    }

    private fun actualizarAviso() {

        val fechaPantalla =
            editTextFechaEditarAviso.text.toString().trim()

        val hora =
            editTextHoraEditarAviso.text.toString().trim()

        val direccion =
            editTextDireccionEditarAviso.text.toString().trim()

        val telefono =
            editTextTelefonoEditarAviso.text.toString().trim()

        val mensaje =
            editTextMensajeEditarAviso.text.toString().trim()

        val observacion =
            editTextObservacionEditarAviso.text.toString().trim()

        if (fechaPantalla.isEmpty()) {

            Toast.makeText(
                this,
                "Ingresá una fecha",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (hora.isEmpty()) {

            Toast.makeText(
                this,
                "Ingresá una hora",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (direccion.isEmpty()) {

            Toast.makeText(
                this,
                "Ingresá una dirección",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (telefono.isEmpty()) {

            Toast.makeText(
                this,
                "Ingresá un teléfono",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (clienteSeleccionado == null) {

            Toast.makeText(
                this,
                "Seleccioná un cliente",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (operarioSeleccionado == null) {

            Toast.makeText(
                this,
                "Seleccioná un operario",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val fecha: String

        try {

            val formatoPantalla =
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                )

            val formatoBackend =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            val fechaConvertida =
                formatoPantalla.parse(fechaPantalla)

            if (fechaConvertida == null) {
                throw Exception()
            }

            fecha =
                formatoBackend.format(
                    fechaConvertida
                )

        } catch (_: Exception) {

            Toast.makeText(
                this,
                "La fecha no es válida",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val urgenciaSeleccionada =
            spinnerUrgenciaEditarAviso
                .selectedItem
                .toString()
                .lowercase()

        val request =
            AvisoRequest(
                fecha = fecha,
                hora = hora,
                direccion = direccion,
                telefono = telefono,
                mensaje =
                    if (mensaje.isEmpty()) {
                        null
                    } else {
                        mensaje
                    },
                observacion =
                    if (observacion.isEmpty()) {
                        null
                    } else {
                        observacion
                    },
                estado = estadoAviso,
                urgencia = urgenciaSeleccionada,
                usuario_id = operarioSeleccionado!!.id,
                cliente_id = clienteSeleccionado!!.id
            )

        mostrarCargando(true)

        RetrofitClient.api
            .actualizarAviso(
                avisoId,
                request
            )
            .enqueue(
                object : Callback<AvisoResponse> {

                    override fun onResponse(
                        call: Call<AvisoResponse>,
                        response: Response<AvisoResponse>
                    ) {

                        mostrarCargando(false)

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@EditarAvisoActivity,
                                "Aviso actualizado con éxito",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {

                            Log.e(
                                "EDITAR_AVISO",
                                "Error HTTP: ${response.code()}"
                            )

                            Toast.makeText(
                                this@EditarAvisoActivity,
                                "No se pudo actualizar el aviso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<AvisoResponse>,
                        t: Throwable
                    ) {

                        mostrarCargando(false)

                        Log.e(
                            "EDITAR_AVISO",
                            "Error al actualizar aviso",
                            t
                        )

                        Toast.makeText(
                            this@EditarAvisoActivity,
                            "Error de conexión",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    private fun mostrarConfirmacionEliminar() {

        AlertDialog.Builder(this)
            .setTitle("Eliminar aviso")
            .setMessage(
                "¿Estás seguro de que querés eliminar este aviso?"
            )
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton(
                "Eliminar"
            ) { _, _ ->

                eliminarAviso()
            }
            .show()
    }

    private fun eliminarAviso() {

        mostrarCargando(true)

        RetrofitClient.api
            .eliminarAviso(avisoId)
            .enqueue(
                object : Callback<Void> {

                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>
                    ) {

                        mostrarCargando(false)

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@EditarAvisoActivity,
                                "Aviso eliminado con éxito",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {

                            Log.e(
                                "EDITAR_AVISO",
                                "Error HTTP al eliminar: ${response.code()}"
                            )

                            Toast.makeText(
                                this@EditarAvisoActivity,
                                "No se pudo eliminar el aviso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<Void>,
                        t: Throwable
                    ) {

                        mostrarCargando(false)

                        Log.e(
                            "EDITAR_AVISO",
                            "Error al eliminar aviso",
                            t
                        )

                        Toast.makeText(
                            this@EditarAvisoActivity,
                            "Error de conexión",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    private fun mostrarCargando(
        cargando: Boolean
    ) {

        progressBarEditarAviso.visibility =
            if (cargando) {
                ProgressBar.VISIBLE
            } else {
                ProgressBar.GONE
            }

        buttonGuardarCambiosAviso.isEnabled =
            !cargando

        buttonEliminarAviso.isEnabled =
            !cargando
    }
}