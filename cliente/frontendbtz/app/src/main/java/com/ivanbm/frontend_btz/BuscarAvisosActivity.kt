package com.ivanbm.frontend_btz

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
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
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton
import com.ivanbm.frontend_btz.adapter.ClienteAdapter
import com.ivanbm.frontend_btz.adapter.MaterialAdapter
import com.ivanbm.frontend_btz.adapter.UsuarioAdapter
import com.ivanbm.frontend_btz.model.Cliente
import com.ivanbm.frontend_btz.model.ClientesResponse
import com.ivanbm.frontend_btz.model.Material
import com.ivanbm.frontend_btz.model.MaterialesResponse
import com.ivanbm.frontend_btz.model.Usuario
import com.ivanbm.frontend_btz.model.UsuariosResponse

// Clase personalizada para que el RecyclerView calcule bien su altura dentro del ScrollView
class WrapContentRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthSpec, expandSpec)
    }
}

class BuscarAvisosActivity : AppCompatActivity() {
    private lateinit var contenedorSeccionesBusqueda: LinearLayout
    private lateinit var buttonSeccionAvisos: MaterialButton
    private lateinit var buttonSeccionClientes: MaterialButton
    private lateinit var buttonSeccionMateriales: MaterialButton
    private lateinit var buttonSeccionUsuarios: MaterialButton
    private lateinit var scrollViewContenidoBuscarAvisos: ScrollView
    private lateinit var textViewTituloResultadosBuscarAvisos: TextView
    private lateinit var buttonVolverBuscarAvisos: ImageButton
    private lateinit var buttonMostrarFiltrosAvisos: Button
    private lateinit var buttonEjecutarBusquedaAvisos: Button
    private lateinit var buttonAplicarFiltrosAvisos: Button
    private lateinit var buttonLimpiarFiltrosAvisos: Button
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
    // Limita las páginas adicionales que se cargan automáticamente.
    private var paginasCargaAutomatica = 0
    private val MAX_PAGINAS_CARGA_AUTOMATICA = 2
    private var busquedaActual: FiltrosBusqueda? = null
    private var busquedaIniciada = false
    private var totalRegistrosTotal = 0 // Total fijo de la API para que no fluctúe el contador

    private val editorAvisoLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (busquedaIniciada && busquedaActual != null) {
                iniciarBusqueda(busquedaActual!!)
            }
        }
    private val editorClienteLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (
                seccionActiva == SeccionBusqueda.CLIENTES &&
                busquedaIniciada
            ) {
                iniciarBusquedaEntidad(
                    terminoBusquedaGeneralActual.orEmpty()
                )
            }
        }

    private val editorMaterialLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (
                seccionActiva == SeccionBusqueda.MATERIALES &&
                busquedaIniciada
            ) {
                iniciarBusquedaEntidad(
                    terminoBusquedaGeneralActual.orEmpty()
                )
            }
        }
    private val editorUsuarioLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (
                seccionActiva == SeccionBusqueda.USUARIOS &&
                busquedaIniciada
            ) {
                iniciarBusquedaEntidad(
                    terminoBusquedaGeneralActual.orEmpty()
                )
            }
        }
    private lateinit var cardFiltrosAvanzadosBuscarAvisos: androidx.cardview.widget.CardView

    // FILTROS AVANZADOS DE CLIENTES
    private lateinit var buttonMostrarFiltrosClientes: Button
    private lateinit var buttonAplicarFiltrosClientes: Button
    private lateinit var buttonLimpiarFiltrosClientes: Button
    private lateinit var cardFiltrosAvanzadosBuscarClientes: androidx.cardview.widget.CardView

    private lateinit var editTextNumeroFiltroClientes: EditText
    private lateinit var editTextNombreFiltroClientes: EditText
    private lateinit var editTextEmailFiltroClientes: EditText
    private lateinit var radioGroupAseguradoFiltroClientes: RadioGroup

    // FILTROS AVANZADOS DE MATERIALES
    private lateinit var buttonMostrarFiltrosMateriales: Button
    private lateinit var buttonAplicarFiltrosMateriales: Button
    private lateinit var buttonLimpiarFiltrosMateriales: Button
    private lateinit var cardFiltrosAvanzadosBuscarMateriales: androidx.cardview.widget.CardView

    private lateinit var editTextNumeroFiltroMateriales: EditText
    private lateinit var editTextNombreFiltroMateriales: EditText

    // FILTROS AVANZADOS DE USUARIOS
    private lateinit var buttonMostrarFiltrosUsuarios: Button
    private lateinit var buttonAplicarFiltrosUsuarios: Button
    private lateinit var buttonLimpiarFiltrosUsuarios: Button
    private lateinit var cardFiltrosAvanzadosBuscarUsuarios: androidx.cardview.widget.CardView
    private lateinit var editTextNumeroFiltroUsuarios: EditText
    private lateinit var editTextNombreFiltroUsuarios: EditText
    private lateinit var editTextDniFiltroUsuarios: EditText
    private lateinit var radioGroupRolFiltroUsuarios: RadioGroup
    private data class FiltrosBusqueda(
        val buscar: String? = null,
        val direccion: String? = null,
        val telefono: String? = null,
        val avisoId: Int? = null,
        val fecha: String? = null,
        val hora: String? = null,
        val estado: String? = null
    )
    private data class FiltrosClientes(
        val clienteId: Int? = null,
        val nombre: String? = null,
        val email: String? = null,
        val asegurado: Boolean? = null
    )
    private data class FiltrosMateriales(
        val materialId: Int? = null,
        val nombre: String? = null
    )
    private var filtrosMaterialesActuales = FiltrosMateriales()
    private data class FiltrosUsuarios(
        val usuarioId: Int? = null,
        val nombre: String? = null,
        val dni: String? = null,
        val rol: String? = null
    )
    private var filtrosUsuariosActuales = FiltrosUsuarios()
    private var filtrosClientesActuales = FiltrosClientes()
    private enum class SeccionBusqueda {
        AVISOS,
        CLIENTES,
        MATERIALES,
        USUARIOS
    }
    private var seccionActiva = SeccionBusqueda.AVISOS
    private lateinit var clienteAdapter: ClienteAdapter
    private lateinit var materialAdapter: MaterialAdapter
    private lateinit var usuarioAdapter: UsuarioAdapter
    private val clientesResultado = mutableListOf<Cliente>()
    private val materialesResultado = mutableListOf<Material>()
    private val usuariosResultado = mutableListOf<Usuario>()
    private var terminoBusquedaGeneralActual: String? = null
    private fun actualizarTituloResultados(cantidad: Int) {
        textViewTituloResultadosBuscarAvisos.text = when (seccionActiva) {
            SeccionBusqueda.AVISOS -> "Resultados de búsqueda ($cantidad)"
            SeccionBusqueda.CLIENTES -> "Resultados de clientes ($cantidad)"
            SeccionBusqueda.MATERIALES -> "Resultados de materiales ($cantidad)"
            SeccionBusqueda.USUARIOS -> "Resultados de usuarios ($cantidad)"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_avisos)

        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        val vistaPrincipal = findViewById<View>(
            R.id.layoutPrincipalBuscarAvisos
        )

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(
            vistaPrincipal
        ) { vista, insets ->
            val barrasSistema = insets.getInsets(
                androidx.core.view.WindowInsetsCompat.Type.statusBars()
            )

            vista.setPadding(
                vista.paddingLeft,
                barrasSistema.top,
                vista.paddingRight,
                vista.paddingBottom
            )

            insets
        }
        inicializarVistas()
        configurarRecyclerView()
        configurarSelectorSecciones()
        configurarBotonVolver()
        configurarFiltrosAvanzados()
        configurarSelectorFecha()
        configurarSelectorHora()
        configurarBotonesBusqueda()
        configurarPaginacion()
        configurarTecladoBusqueda()
    }

    private fun inicializarVistas() {
        contenedorSeccionesBusqueda =
            findViewById(R.id.contenedorSeccionesBusqueda)

        buttonSeccionAvisos =
            findViewById(R.id.buttonSeccionAvisos)

        buttonSeccionClientes =
            findViewById(R.id.buttonSeccionClientes)

        buttonSeccionMateriales =
            findViewById(R.id.buttonSeccionMateriales)

        buttonSeccionUsuarios =
            findViewById(R.id.buttonSeccionUsuarios)

        scrollViewContenidoBuscarAvisos =
            findViewById(R.id.scrollViewContenidoBuscarAvisos)

        textViewTituloResultadosBuscarAvisos =
            findViewById(R.id.textViewTituloResultadosBuscarAvisos)
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

        cardFiltrosAvanzadosBuscarAvisos =
            findViewById(R.id.cardFiltrosAvanzadosBuscarAvisos)

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

        // FILTROS AVANZADOS DE CLIENTES
        buttonMostrarFiltrosClientes =
            findViewById(R.id.buttonMostrarFiltrosClientes)

        buttonAplicarFiltrosClientes =
            findViewById(R.id.buttonAplicarFiltrosClientes)

        buttonLimpiarFiltrosClientes =
            findViewById(R.id.buttonLimpiarFiltrosClientes)

        cardFiltrosAvanzadosBuscarClientes =
            findViewById(R.id.cardFiltrosAvanzadosBuscarClientes)

        editTextNumeroFiltroClientes =
            findViewById(R.id.editTextNumeroFiltroClientes)

        editTextNombreFiltroClientes =
            findViewById(R.id.editTextNombreFiltroClientes)

        editTextEmailFiltroClientes =
            findViewById(R.id.editTextEmailFiltroClientes)

        radioGroupAseguradoFiltroClientes =
            findViewById(R.id.radioGroupAseguradoFiltroClientes)

        // FILTROS AVANZADOS DE MATERIALES
        buttonMostrarFiltrosMateriales =
            findViewById(R.id.buttonMostrarFiltrosMateriales)

        buttonAplicarFiltrosMateriales =
            findViewById(R.id.buttonAplicarFiltrosMateriales)

        buttonLimpiarFiltrosMateriales =
            findViewById(R.id.buttonLimpiarFiltrosMateriales)

        cardFiltrosAvanzadosBuscarMateriales =
            findViewById(R.id.cardFiltrosAvanzadosBuscarMateriales)

        editTextNumeroFiltroMateriales =
            findViewById(R.id.editTextNumeroFiltroMateriales)

        editTextNombreFiltroMateriales =
            findViewById(R.id.editTextNombreFiltroMateriales)

        // FILTROS AVANZADOS DE USUARIOS
        buttonMostrarFiltrosUsuarios =
            findViewById(R.id.buttonMostrarFiltrosUsuarios)

        buttonAplicarFiltrosUsuarios =
            findViewById(R.id.buttonAplicarFiltrosUsuarios)

        buttonLimpiarFiltrosUsuarios =
            findViewById(R.id.buttonLimpiarFiltrosUsuarios)

        cardFiltrosAvanzadosBuscarUsuarios =
            findViewById(R.id.cardFiltrosAvanzadosBuscarUsuarios)

        editTextNumeroFiltroUsuarios =
            findViewById(R.id.editTextNumeroFiltroUsuarios)

        editTextNombreFiltroUsuarios =
            findViewById(R.id.editTextNombreFiltroUsuarios)

        editTextDniFiltroUsuarios =
            findViewById(R.id.editTextDniFiltroUsuarios)

        radioGroupRolFiltroUsuarios =
            findViewById(R.id.radioGroupRolFiltroUsuarios)
    }

    private fun configurarSelectorSecciones() {
        val modoOperario =
            intent.getStringExtra("MODO_BUSQUEDA") == "OPERARIO"

        if (modoOperario) {
            contenedorSeccionesBusqueda.visibility = View.GONE
            return
        }

        val botonesSeccion = listOf(
            buttonSeccionAvisos,
            buttonSeccionClientes,
            buttonSeccionMateriales,
            buttonSeccionUsuarios
        )

        fun actualizarSeccionActiva(
            botonSeleccionado: MaterialButton,
            nuevaSeccion: SeccionBusqueda,
            textoBusqueda: String
        ) {
            val colorVioleta = Color.parseColor("#2F2483")
            val colorFondoInactivo = Color.parseColor("#EAE8F5")

            botonesSeccion.forEach { boton ->
                val seleccionado = boton == botonSeleccionado

                boton.backgroundTintList = ColorStateList.valueOf(
                    if (seleccionado) colorVioleta else colorFondoInactivo
                )

                boton.setTextColor(
                    if (seleccionado) Color.WHITE else colorVioleta
                )

                boton.iconTint = ColorStateList.valueOf(
                    if (seleccionado) Color.WHITE else colorVioleta
                )

                boton.isSelected = seleccionado
            }

            seccionActiva = nuevaSeccion
            editTextBusquedaGeneralAvisos.hint = textoBusqueda

            buttonMostrarFiltrosAvisos.visibility =
                if (nuevaSeccion == SeccionBusqueda.AVISOS) View.VISIBLE else View.GONE

            buttonMostrarFiltrosClientes.visibility =
                if (nuevaSeccion == SeccionBusqueda.CLIENTES) View.VISIBLE else View.GONE

            buttonMostrarFiltrosMateriales.visibility =
                if (nuevaSeccion == SeccionBusqueda.MATERIALES) View.VISIBLE else View.GONE

            buttonMostrarFiltrosUsuarios.visibility =
                if (nuevaSeccion == SeccionBusqueda.USUARIOS) View.VISIBLE else View.GONE

            cardFiltrosAvanzadosBuscarAvisos.visibility = View.GONE
            cardFiltrosAvanzadosBuscarClientes.visibility = View.GONE
            cardFiltrosAvanzadosBuscarMateriales.visibility = View.GONE
            cardFiltrosAvanzadosBuscarUsuarios.visibility = View.GONE

            recyclerViewResultadosBuscarAvisos.adapter = when (nuevaSeccion) {
                SeccionBusqueda.AVISOS -> avisoAdapter
                SeccionBusqueda.CLIENTES -> clienteAdapter
                SeccionBusqueda.MATERIALES -> materialAdapter
                SeccionBusqueda.USUARIOS -> usuarioAdapter
            }

            totalRegistrosTotal = 0
            actualizarTituloResultados(0)

            avisoAdapter.actualizarAvisos(emptyList())
            clienteAdapter.actualizarClientes(emptyList())
            materialAdapter.actualizarMateriales(emptyList())
            usuarioAdapter.actualizarUsuarios(emptyList())

            clientesResultado.clear()
            materialesResultado.clear()
            usuariosResultado.clear()
            paginasCargaAutomatica = 0
            paginaActual = 1
            cargandoAvisos = false
            hayMasAvisos = true
            busquedaIniciada = false
            busquedaActual = null
            terminoBusquedaGeneralActual = null
        }

        buttonSeccionAvisos.setOnClickListener {
            actualizarSeccionActiva(
                buttonSeccionAvisos,
                SeccionBusqueda.AVISOS,
                "Dirección, teléfono o N.º de aviso"
            )
        }

        buttonSeccionClientes.setOnClickListener {
            actualizarSeccionActiva(
                buttonSeccionClientes,
                SeccionBusqueda.CLIENTES,
                "Nombre, email o N.º de cliente"
            )
        }

        buttonSeccionMateriales.setOnClickListener {
            actualizarSeccionActiva(
                buttonSeccionMateriales,
                SeccionBusqueda.MATERIALES,
                "Nombre o N.º de material"
            )
        }

        buttonSeccionUsuarios.setOnClickListener {
            actualizarSeccionActiva(
                buttonSeccionUsuarios,
                SeccionBusqueda.USUARIOS,
                "Nombre, DNI o N.º de usuario"
            )
        }

        actualizarSeccionActiva(
            buttonSeccionAvisos,
            SeccionBusqueda.AVISOS,
            "Dirección, teléfono o N.º de aviso"
        )
    }

    private fun configurarRecyclerView() {
        avisoAdapter = AvisoAdapter(
            emptyList(),
            onAvisoClick = { aviso ->
                abrirAvisoSeleccionado(aviso)
            }
        )
        clienteAdapter = ClienteAdapter(emptyList()) { cliente ->
            val intentEdicion = Intent(
                this,
                EditarClienteActivity::class.java
            )
            intentEdicion.putExtra("CLIENTE_ID", cliente.id)
            editorClienteLauncher.launch(intentEdicion)
        }

        materialAdapter = MaterialAdapter(emptyList()) { material ->
            val intentEdicion = Intent(
                this,
                EditarMaterialActivity::class.java
            )
            intentEdicion.putExtra("material_id", material.id)
            editorMaterialLauncher.launch(intentEdicion)
        }

        usuarioAdapter = UsuarioAdapter(emptyList()) { usuario ->
            val intentEdicion = Intent(
                this,
                EditarUsuarioActivity::class.java
            )
            intentEdicion.putExtra("USUARIO_ID", usuario.id)
            editorUsuarioLauncher.launch(intentEdicion)
        }

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
                cardFiltrosAvanzadosBuscarAvisos.visibility != View.VISIBLE

            cardFiltrosAvanzadosBuscarAvisos.visibility =
                if (mostrarFiltros) View.VISIBLE else View.GONE

            buttonMostrarFiltrosAvisos.text =
                if (mostrarFiltros) "Filtros avanzados ▲"
                else "Filtros avanzados ▼"
        }

        buttonMostrarFiltrosClientes.setOnClickListener {
            val mostrarFiltros =
                cardFiltrosAvanzadosBuscarClientes.visibility != View.VISIBLE

            cardFiltrosAvanzadosBuscarClientes.visibility =
                if (mostrarFiltros) View.VISIBLE else View.GONE

            buttonMostrarFiltrosClientes.text =
                if (mostrarFiltros) "Filtros avanzados ▲"
                else "Filtros avanzados ▼"
        }

        buttonMostrarFiltrosMateriales.setOnClickListener {
            val mostrarFiltros =
                cardFiltrosAvanzadosBuscarMateriales.visibility != View.VISIBLE

            cardFiltrosAvanzadosBuscarMateriales.visibility =
                if (mostrarFiltros) View.VISIBLE else View.GONE

            buttonMostrarFiltrosMateriales.text =
                if (mostrarFiltros) "Filtros avanzados ▲"
                else "Filtros avanzados ▼"
        }

        buttonMostrarFiltrosUsuarios.setOnClickListener {
            val mostrarFiltros =
                cardFiltrosAvanzadosBuscarUsuarios.visibility != View.VISIBLE

            cardFiltrosAvanzadosBuscarUsuarios.visibility =
                if (mostrarFiltros) View.VISIBLE else View.GONE

            buttonMostrarFiltrosUsuarios.text =
                if (mostrarFiltros) "Filtros avanzados ▲"
                else "Filtros avanzados ▼"
        }
    }

    private fun crearFiltrosClientes(): FiltrosClientes? {
        val numeroTexto = editTextNumeroFiltroClientes.text
            .toString()
            .trim()

        val clienteId = if (numeroTexto.isNotEmpty()) {
            numeroTexto.toIntOrNull() ?: run {
                editTextNumeroFiltroClientes.error =
                    "Ingresá un número de cliente válido"
                return null
            }
        } else {
            null
        }

        val nombre = editTextNombreFiltroClientes.text
            .toString()
            .trim()
            .takeIf { it.isNotEmpty() }

        val email = editTextEmailFiltroClientes.text
            .toString()
            .trim()
            .takeIf { it.isNotEmpty() }

        val asegurado = when (
            radioGroupAseguradoFiltroClientes.checkedRadioButtonId
        ) {
            R.id.radioButtonAseguradoSiClientes -> true
            R.id.radioButtonAseguradoNoClientes -> false
            else -> null
        }

        return FiltrosClientes(
            clienteId = clienteId,
            nombre = nombre,
            email = email,
            asegurado = asegurado
        )
    }

    private fun crearFiltrosMateriales(): FiltrosMateriales? {
        val numeroTexto = editTextNumeroFiltroMateriales.text
            .toString()
            .trim()

        val materialId = if (numeroTexto.isNotEmpty()) {
            numeroTexto.toIntOrNull() ?: run {
                editTextNumeroFiltroMateriales.error =
                    "Ingresá un número de material válido"
                return null
            }
        } else {
            null
        }

        val nombre = editTextNombreFiltroMateriales.text
            .toString()
            .trim()
            .takeIf { it.isNotEmpty() }

        return FiltrosMateriales(
            materialId = materialId,
            nombre = nombre
        )
    }

    private fun crearFiltrosUsuarios(): FiltrosUsuarios? {
        val numeroTexto = editTextNumeroFiltroUsuarios.text
            .toString()
            .trim()

        val usuarioId = if (numeroTexto.isNotEmpty()) {
            numeroTexto.toIntOrNull() ?: run {
                editTextNumeroFiltroUsuarios.error =
                    "Ingresá un número de usuario válido"
                return null
            }
        } else {
            null
        }

        val nombre = editTextNombreFiltroUsuarios.text
            .toString()
            .trim()
            .takeIf { it.isNotEmpty() }

        val dni = editTextDniFiltroUsuarios.text
            .toString()
            .trim()
            .takeIf { it.isNotEmpty() }

        val rol = when (
            radioGroupRolFiltroUsuarios.checkedRadioButtonId
        ) {
            R.id.radioButtonRolAdministradorUsuarios -> "admin"
            R.id.radioButtonRolOperarioUsuarios -> "operario"
            else -> null
        }

        return FiltrosUsuarios(
            usuarioId = usuarioId,
            nombre = nombre,
            dni = dni,
            rol = rol
        )
    }

    private fun iniciarBusquedaEntidad(termino: String) {
        terminoBusquedaGeneralActual = termino
        busquedaIniciada = true
        paginasCargaAutomatica = 0
        paginaActual = 1
        hayMasAvisos = true
        cargandoAvisos = false
        totalRegistrosTotal = 0

        clientesResultado.clear()
        materialesResultado.clear()
        usuariosResultado.clear()

        when (seccionActiva) {
            SeccionBusqueda.AVISOS -> {
                iniciarBusqueda(
                    FiltrosBusqueda(
                        buscar = termino,
                        estado = obtenerEstadoSeleccionado()
                    )
                )
                return
            }

            SeccionBusqueda.CLIENTES ->
                clienteAdapter.actualizarClientes(emptyList())

            SeccionBusqueda.MATERIALES ->
                materialAdapter.actualizarMateriales(emptyList())

            SeccionBusqueda.USUARIOS ->
                usuarioAdapter.actualizarUsuarios(emptyList())
        }

        recyclerViewResultadosBuscarAvisos.scrollToPosition(0)
        cargarEntidad()
    }

    private fun cargarEntidad() {
        if (seccionActiva == SeccionBusqueda.AVISOS) {
            cargarAvisos()
            return
        }

        if (cargandoAvisos || !hayMasAvisos) return

        cargandoAvisos = true
        progressBarBuscarAvisos.visibility = View.VISIBLE

        when (seccionActiva) {
            SeccionBusqueda.AVISOS -> {
                cargarAvisos()
                return
            }

            SeccionBusqueda.CLIENTES -> {
                val filtros = filtrosClientesActuales
                val hayFiltrosAvanzados =
                    filtros.clienteId != null ||
                            filtros.nombre != null ||
                            filtros.email != null ||
                            filtros.asegurado != null

                val termino = terminoBusquedaGeneralActual.orEmpty().trim()

                val id: Int?
                val nombre: String?
                val email: String?

                if (hayFiltrosAvanzados) {
                    id = filtros.clienteId
                    nombre = filtros.nombre
                    email = filtros.email
                } else {
                    id = termino.toIntOrNull()
                    email = if (id == null && termino.contains("@")) termino else null
                    nombre = if (id == null && email == null) termino.takeIf { it.isNotBlank() } else null
                }

                RetrofitClient.api.obtenerClientes(
                    pagina = paginaActual,
                    clienteId = id,
                    nombre = nombre,
                    email = email,
                    asegurado = filtros.asegurado
                ).enqueue(object : Callback<ClientesResponse> {

                    override fun onResponse(
                        call: Call<ClientesResponse>,
                        response: Response<ClientesResponse>
                    ) {
                        progressBarBuscarAvisos.visibility = View.GONE
                        cargandoAvisos = false

                        if (!response.isSuccessful) {
                            mostrarErrorBusqueda("clientes", response.code())
                            return
                        }

                        val respuesta = response.body()

                        if (respuesta == null || !respuesta.status) {
                            mostrarMensajeBusqueda("No se pudieron obtener los clientes")
                            return
                        }

                        if (paginaActual == 1) {
                            totalRegistrosTotal = respuesta.meta.total
                            clienteAdapter.actualizarClientes(respuesta.data)
                            actualizarTituloResultados(totalRegistrosTotal)
                            desplzarAResultados()

                            if (respuesta.data.isEmpty()) {
                                mostrarMensajeBusqueda("No se encontraron clientes")
                            }
                        } else {
                            clienteAdapter.agregarClientes(respuesta.data)
                            actualizarTituloResultados(totalRegistrosTotal)
                        }

                        hayMasAvisos =
                            respuesta.meta.current_page < respuesta.meta.last_page

                        paginaActual = respuesta.meta.current_page + 1
                        recyclerViewResultadosBuscarAvisos.post {
                            comprobarSiFaltaContenidoParaDesplazar()
                        }
                    }

                    override fun onFailure(
                        call: Call<ClientesResponse>,
                        t: Throwable
                    ) {
                        manejarErrorConexion(t)
                    }
                })
            }
            SeccionBusqueda.MATERIALES -> {
                val filtros = filtrosMaterialesActuales
                val hayFiltrosAvanzados =
                    filtros.materialId != null || filtros.nombre != null

                val termino = terminoBusquedaGeneralActual.orEmpty().trim()

                val id: Int?
                val nombre: String?

                if (hayFiltrosAvanzados) {
                    id = filtros.materialId
                    nombre = filtros.nombre
                } else {
                    id = termino.toIntOrNull()
                    nombre = if (id == null) termino.takeIf { it.isNotBlank() } else null
                }

                RetrofitClient.api.obtenerMateriales(
                    pagina = paginaActual,
                    materialId = id,
                    nombre = nombre
                ).enqueue(object : Callback<MaterialesResponse> {

                    override fun onResponse(
                        call: Call<MaterialesResponse>,
                        response: Response<MaterialesResponse>
                    ) {
                        progressBarBuscarAvisos.visibility = View.GONE
                        cargandoAvisos = false

                        if (!response.isSuccessful) {
                            mostrarErrorBusqueda("materiales", response.code())
                            return
                        }

                        val respuesta = response.body()

                        if (respuesta == null || !respuesta.status) {
                            mostrarMensajeBusqueda("No se pudieron obtener los materiales")
                            return
                        }

                        if (paginaActual == 1) {
                            totalRegistrosTotal = respuesta.meta.total
                            materialAdapter.actualizarMateriales(respuesta.data)
                            actualizarTituloResultados(totalRegistrosTotal)
                            desplzarAResultados()

                            if (respuesta.data.isEmpty()) {
                                mostrarMensajeBusqueda("No se encontraron materiales")
                            }
                        } else {
                            materialAdapter.agregarMateriales(respuesta.data)
                            actualizarTituloResultados(totalRegistrosTotal)
                        }

                        hayMasAvisos =
                            respuesta.meta.current_page < respuesta.meta.last_page

                        paginaActual = respuesta.meta.current_page + 1
                        recyclerViewResultadosBuscarAvisos.post {
                            comprobarSiFaltaContenidoParaDesplazar()
                        }
                    }

                    override fun onFailure(
                        call: Call<MaterialesResponse>,
                        t: Throwable
                    ) {
                        manejarErrorConexion(t)
                    }
                })
            }
            SeccionBusqueda.USUARIOS -> {
                val filtros = filtrosUsuariosActuales
                val hayFiltrosAvanzados =
                    filtros.usuarioId != null ||
                            filtros.nombre != null ||
                            filtros.dni != null ||
                            filtros.rol != null

                val termino = terminoBusquedaGeneralActual.orEmpty().trim()

                val id: Int?
                val nombre: String?
                val dni: String?
                val rol: String?

                if (hayFiltrosAvanzados) {
                    id = filtros.usuarioId
                    nombre = filtros.nombre
                    dni = filtros.dni
                    rol = filtros.rol
                } else {
                    val esDni = termino.matches(Regex("\\d{7,8}"))
                    id = if (esDni) null else termino.toIntOrNull()
                    dni = if (esDni) termino else null
                    nombre = if (id == null && dni == null) termino.takeIf { it.isNotBlank() } else null
                    rol = null
                }

                RetrofitClient.api.obtenerUsuarios(
                    pagina = paginaActual,
                    usuarioId = id,
                    nombre = nombre,
                    dni = dni,
                    rol = rol
                ).enqueue(object : Callback<UsuariosResponse> {

                    override fun onResponse(
                        call: Call<UsuariosResponse>,
                        response: Response<UsuariosResponse>
                    ) {
                        progressBarBuscarAvisos.visibility = View.GONE
                        cargandoAvisos = false

                        if (!response.isSuccessful) {
                            mostrarErrorBusqueda("usuarios", response.code())
                            return
                        }

                        val respuesta = response.body()

                        if (respuesta == null || !respuesta.status) {
                            mostrarMensajeBusqueda("No se pudieron obtener los usuarios")
                            return
                        }

                        if (paginaActual == 1) {
                            usuariosResultado.clear()
                            totalRegistrosTotal = respuesta.meta?.total ?: respuesta.data.size
                        }

                        usuariosResultado.addAll(respuesta.data)
                        usuarioAdapter.actualizarUsuarios(usuariosResultado.toList())

                        actualizarTituloResultados(totalRegistrosTotal)

                        if (paginaActual == 1) {
                            desplzarAResultados()
                            if (respuesta.data.isEmpty()) {
                                mostrarMensajeBusqueda("No se encontraron usuarios")
                            }
                        }

                        val meta = respuesta.meta
                        if (meta != null) {
                            hayMasAvisos = meta.current_page < meta.last_page
                            paginaActual = meta.current_page + 1
                        } else {
                            hayMasAvisos = false
                        }

                        recyclerViewResultadosBuscarAvisos.post {
                            comprobarSiFaltaContenidoParaDesplazar()
                        }
                    }

                    override fun onFailure(
                        call: Call<UsuariosResponse>,
                        t: Throwable
                    ) {
                        manejarErrorConexion(t)
                    }
                })
            }
        }
    }

    private fun mostrarErrorBusqueda(
        entidad: String,
        codigoHttp: Int
    ) {
        mostrarMensajeBusqueda(
            "Error al buscar $entidad: HTTP $codigoHttp"
        )
    }

    private fun mostrarMensajeBusqueda(mensaje: String) {
        Toast.makeText(
            this@BuscarAvisosActivity,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun manejarErrorConexion(error: Throwable) {
        progressBarBuscarAvisos.visibility = View.GONE
        cargandoAvisos = false

        Toast.makeText(
            this@BuscarAvisosActivity,
            "Error de conexión: ${error.message}",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun configurarBotonesBusqueda() {
        buttonEjecutarBusquedaAvisos.setOnClickListener {
            val termino = editTextBusquedaGeneralAvisos.text
                .toString()
                .trim()

            if (termino.isEmpty()) {
                editTextBusquedaGeneralAvisos.error =
                    "Ingresá qué querés buscar"
                return@setOnClickListener
            }
            if (seccionActiva == SeccionBusqueda.AVISOS) {
                val filtros = FiltrosBusqueda(
                    buscar = termino,
                    estado = obtenerEstadoSeleccionado()
                )

                iniciarBusqueda(filtros)
            } else {
                when (seccionActiva) {
                    SeccionBusqueda.CLIENTES -> {
                        filtrosClientesActuales = FiltrosClientes()
                    }
                    SeccionBusqueda.MATERIALES -> {
                        filtrosMaterialesActuales = FiltrosMateriales()
                    }
                    SeccionBusqueda.USUARIOS -> {
                        filtrosUsuariosActuales = FiltrosUsuarios()
                    }
                    SeccionBusqueda.AVISOS -> Unit
                }

                iniciarBusquedaEntidad(termino)
            }
        }
        buttonAplicarFiltrosAvisos.setOnClickListener {
            val filtros = crearFiltrosAvanzados() ?: return@setOnClickListener

            val todosLosEstadosSeleccionados =
                radioGroupEstadoFiltroAvisos.checkedRadioButtonId ==
                        R.id.radioButtonTodosEstadosAvisos

            if (filtros.buscar == null &&
                filtros.direccion == null &&
                filtros.telefono == null &&
                filtros.avisoId == null &&
                filtros.fecha == null &&
                filtros.hora == null &&
                filtros.estado == null &&
                !todosLosEstadosSeleccionados
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

        buttonAplicarFiltrosClientes.setOnClickListener {
            val filtros = crearFiltrosClientes() ?: return@setOnClickListener

            val hayFiltros =
                filtros.clienteId != null ||
                        filtros.nombre != null ||
                        filtros.email != null ||
                        filtros.asegurado != null

            if (!hayFiltros) {
                Toast.makeText(
                    this,
                    "Seleccioná al menos un criterio de búsqueda",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            filtrosClientesActuales = filtros
            editTextBusquedaGeneralAvisos.text.clear()
            iniciarBusquedaEntidad("")
        }

        buttonLimpiarFiltrosClientes.setOnClickListener {
            editTextNumeroFiltroClientes.text.clear()
            editTextNombreFiltroClientes.text.clear()
            editTextEmailFiltroClientes.text.clear()

            radioGroupAseguradoFiltroClientes.check(
                R.id.radioButtonTodosAseguradoClientes
            )

            editTextBusquedaGeneralAvisos.text.clear()
            filtrosClientesActuales = FiltrosClientes()
            iniciarBusquedaEntidad("")
        }

        buttonAplicarFiltrosMateriales.setOnClickListener {
            val filtros = crearFiltrosMateriales() ?: return@setOnClickListener

            val hayFiltros =
                filtros.materialId != null || filtros.nombre != null

            if (!hayFiltros) {
                Toast.makeText(
                    this,
                    "Ingresá al menos un criterio para buscar materiales",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            filtrosMaterialesActuales = filtros
            editTextBusquedaGeneralAvisos.text.clear()
            iniciarBusquedaEntidad("")
        }

        buttonLimpiarFiltrosMateriales.setOnClickListener {
            editTextNumeroFiltroMateriales.text.clear()
            editTextNombreFiltroMateriales.text.clear()
            editTextBusquedaGeneralAvisos.text.clear()

            filtrosMaterialesActuales = FiltrosMateriales()
            iniciarBusquedaEntidad("")
        }

        buttonAplicarFiltrosUsuarios.setOnClickListener {
            val filtros = crearFiltrosUsuarios() ?: return@setOnClickListener

            val hayFiltros =
                filtros.usuarioId != null ||
                        filtros.nombre != null ||
                        filtros.dni != null ||
                        filtros.rol != null

            if (!hayFiltros) {
                Toast.makeText(
                    this,
                    "Seleccioná al menos un criterio para buscar usuarios",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            filtrosUsuariosActuales = filtros
            editTextBusquedaGeneralAvisos.text.clear()
            iniciarBusquedaEntidad("")
        }

        buttonLimpiarFiltrosUsuarios.setOnClickListener {
            editTextNumeroFiltroUsuarios.text.clear()
            editTextNombreFiltroUsuarios.text.clear()
            editTextDniFiltroUsuarios.text.clear()

            radioGroupRolFiltroUsuarios.check(
                R.id.radioButtonTodosRolesUsuarios
            )

            editTextBusquedaGeneralAvisos.text.clear()
            filtrosUsuariosActuales = FiltrosUsuarios()
            iniciarBusquedaEntidad("")
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
        busquedaIniciada = true
        busquedaActual = filtros
        paginaActual = 1
        hayMasAvisos = true
        cargandoAvisos = false
        totalRegistrosTotal = 0
        paginasCargaAutomatica = 0

        avisoAdapter.actualizarAvisos(emptyList())

        cargarAvisos()
    }

    private fun desplzarAResultados() {
        textViewTituloResultadosBuscarAvisos.post {
            val ubicacionTitulo = IntArray(2)
            val ubicacionScroll = IntArray(2)

            textViewTituloResultadosBuscarAvisos.getLocationOnScreen(ubicacionTitulo)
            scrollViewContenidoBuscarAvisos.getLocationOnScreen(ubicacionScroll)

            val desplazamiento = ubicacionTitulo[1] - ubicacionScroll[1]
            scrollViewContenidoBuscarAvisos.smoothScrollBy(0, desplazamiento.coerceAtLeast(0))
        }
    }

    private fun configurarPaginacion() {
        scrollViewContenidoBuscarAvisos.setOnScrollChangeListener { _: View, _: Int, scrollY: Int, _: Int, _: Int ->
            if (!busquedaIniciada || cargandoAvisos || !hayMasAvisos) {
                return@setOnScrollChangeListener
            }

            val contenido = scrollViewContenidoBuscarAvisos.getChildAt(0) ?: return@setOnScrollChangeListener
            val distanciaAlFinal = contenido.height - (scrollY + scrollViewContenidoBuscarAvisos.height)

            if (distanciaAlFinal <= 200) {
                when (seccionActiva) {
                    SeccionBusqueda.AVISOS -> cargarAvisos()
                    SeccionBusqueda.CLIENTES,
                    SeccionBusqueda.MATERIALES,
                    SeccionBusqueda.USUARIOS -> cargarEntidad()
                }
            }
        }
    }

    private fun comprobarSiFaltaContenidoParaDesplazar() {
        scrollViewContenidoBuscarAvisos.post {
            if (!busquedaIniciada || cargandoAvisos || !hayMasAvisos) {
                return@post
            }

            val contenido =
                scrollViewContenidoBuscarAvisos.getChildAt(0) ?: return@post

            val contenidoDesplazable =
                contenido.height > scrollViewContenidoBuscarAvisos.height

            if (!contenidoDesplazable) {

                // Evita cargar páginas automáticamente sin límite.
                if (paginasCargaAutomatica >= MAX_PAGINAS_CARGA_AUTOMATICA) {
                    return@post
                }

                paginasCargaAutomatica++

                when (seccionActiva) {
                    SeccionBusqueda.AVISOS -> cargarAvisos()

                    SeccionBusqueda.CLIENTES,
                    SeccionBusqueda.MATERIALES,
                    SeccionBusqueda.USUARIOS -> cargarEntidad()
                }
            }
        }
    }

    private fun cargarAvisos() {
        if (cargandoAvisos || !hayMasAvisos) return

        val filtros = busquedaActual ?: FiltrosBusqueda()
        val paginaSolicitada = paginaActual

        cargandoAvisos = true
        progressBarBuscarAvisos.visibility = View.VISIBLE

        RetrofitClient.api.obtenerAvisos(
            pagina = paginaSolicitada,
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

                if (paginaSolicitada == 1) {
                    totalRegistrosTotal = respuesta.meta.total
                    avisoAdapter.actualizarAvisos(respuesta.data)
                    actualizarTituloResultados(totalRegistrosTotal)
                    desplzarAResultados()

                    if (respuesta.data.isEmpty()) {
                        Toast.makeText(
                            this@BuscarAvisosActivity,
                            "No se encontraron avisos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    avisoAdapter.agregarAvisos(respuesta.data)
                    actualizarTituloResultados(totalRegistrosTotal)
                }

                hayMasAvisos =
                    respuesta.meta.current_page < respuesta.meta.last_page

                paginaActual = respuesta.meta.current_page + 1

                recyclerViewResultadosBuscarAvisos.post {
                    comprobarSiFaltaContenidoParaDesplazar()
                }
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
            intentTrabajo.putExtra("AVISO_OPERARIO", aviso.operario.nombre)
            intentTrabajo.putExtra("AVISO_TRABAJO_JSON", Gson().toJson(aviso.trabajo))

            startActivity(intentTrabajo)
            return
        }

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

        val intentEdicion = Intent(this, EditarAvisoActivity::class.java)
        intentEdicion.putExtra("AVISO_ID", aviso.id)
        editorAvisoLauncher.launch(intentEdicion)
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

    private fun configurarTecladoBusqueda() {
        editTextBusquedaGeneralAvisos.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buttonEjecutarBusquedaAvisos.performClick()
                true
            } else {
                false
            }
        }
    }
}