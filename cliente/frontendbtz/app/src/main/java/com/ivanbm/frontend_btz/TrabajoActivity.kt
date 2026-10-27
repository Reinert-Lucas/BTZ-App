package com.ivanbm.frontend_btz

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.ivanbm.frontend_btz.adapter.MaterialTrabajoAdapter
import com.ivanbm.frontend_btz.model.Material
import com.ivanbm.frontend_btz.model.MaterialSeleccionado
import com.ivanbm.frontend_btz.model.MaterialTrabajo
import com.ivanbm.frontend_btz.model.MaterialesResponse
import com.ivanbm.frontend_btz.model.Trabajo
import com.ivanbm.frontend_btz.model.TrabajoRequest
import com.ivanbm.frontend_btz.model.TrabajoResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrabajoActivity : AppCompatActivity() {

    private lateinit var editTextTrabajoRealizado: EditText
    private lateinit var editTextDesperfectoTrabajo: EditText
    private lateinit var textViewOperarioTrabajo: TextView
    private lateinit var textViewDireccionTrabajo: TextView
    private lateinit var textViewFechaTrabajo: TextView
    private lateinit var textViewClienteTrabajo: TextView
    private lateinit var textViewTelefonoTrabajo: TextView
    private lateinit var textViewUrgenciaTrabajo: TextView
    private lateinit var textViewMensajeTrabajo: TextView
    private lateinit var textViewObservacionTrabajo: TextView

    private lateinit var autoCompleteMaterialTrabajo: AutoCompleteTextView
    private lateinit var editTextCantidadMaterialTrabajo: EditText
    private lateinit var buttonAgregarMaterialTrabajo: Button
    private lateinit var buttonFinalizarTrabajo: Button
    private lateinit var recyclerViewMaterialesTrabajo: RecyclerView
    private lateinit var materialTrabajoAdapter: MaterialTrabajoAdapter

    private lateinit var scrollViewTrabajo: android.widget.ScrollView

    private val materialesDisponibles = mutableListOf<Material>()
    private var materialSeleccionado: Material? = null
    private var avisoId: Int = -1
    private var avisoFinalizado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_trabajo)

        scrollViewTrabajo =
            findViewById(R.id.scrollViewTrabajo)

        configurarInsetsTeclado()
        configurarDesplazamientoTeclado()

        inicializarVistas()

        findViewById<ImageButton>(R.id.buttonVolverTrabajo)
            .setOnClickListener {
                finish()
            }

        avisoId = intent.getIntExtra("AVISO_ID", -1)
        avisoFinalizado = intent.getBooleanExtra(
            "AVISO_FINALIZADO",
            false
        )

        mostrarDatosAviso()
        configurarMateriales()

        if (avisoFinalizado) {
            mostrarTrabajoFinalizado()
            configurarModoSoloLectura()
        } else {
            cargarMateriales()
            configurarSeleccionMaterial()
            configurarBotonAgregarMaterial()
            configurarBotonFinalizarTrabajo()
        }
    }

    private fun configurarInsetsTeclado() {

        val paddingInicialIzquierdo = scrollViewTrabajo.paddingLeft
        val paddingInicialSuperior = scrollViewTrabajo.paddingTop
        val paddingInicialDerecho = scrollViewTrabajo.paddingRight
        val paddingInicialInferior = scrollViewTrabajo.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(
            scrollViewTrabajo
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
    }

    private fun configurarDesplazamientoTeclado() {

        scrollViewTrabajo.viewTreeObserver
            .addOnGlobalLayoutListener {

                val campoEnfocado =
                    currentFocus ?: return@addOnGlobalLayoutListener

                if (campoEnfocado !is EditText) {
                    return@addOnGlobalLayoutListener
                }

                if (!campoEnfocado.isShown) {
                    return@addOnGlobalLayoutListener
                }

                val posicionCampo = Rect()

                campoEnfocado.getDrawingRect(posicionCampo)

                scrollViewTrabajo.offsetDescendantRectToMyCoords(
                    campoEnfocado,
                    posicionCampo
                )

                val margen =
                    (16 * resources.displayMetrics.density).toInt()

                val parteSuperiorVisible =
                    scrollViewTrabajo.scrollY + margen

                val parteInferiorVisible =
                    scrollViewTrabajo.scrollY +
                            scrollViewTrabajo.height -
                            scrollViewTrabajo.paddingBottom -
                            margen

                when {

                    posicionCampo.bottom > parteInferiorVisible -> {
                        scrollViewTrabajo.smoothScrollBy(
                            0,
                            posicionCampo.bottom - parteInferiorVisible
                        )
                    }

                    posicionCampo.top < parteSuperiorVisible -> {
                        scrollViewTrabajo.smoothScrollBy(
                            0,
                            posicionCampo.top - parteSuperiorVisible
                        )
                    }
                }
            }
    }

    private fun inicializarVistas() {
        textViewOperarioTrabajo =
            findViewById(R.id.textViewOperarioTrabajo)
        textViewDireccionTrabajo =
            findViewById(R.id.textViewDireccionTrabajo)

        textViewFechaTrabajo =
            findViewById(R.id.textViewFechaTrabajo)

        textViewClienteTrabajo =
            findViewById(R.id.textViewClienteTrabajo)

        textViewTelefonoTrabajo =
            findViewById(R.id.textViewTelefonoTrabajo)

        textViewUrgenciaTrabajo =
            findViewById(R.id.textViewUrgenciaTrabajo)

        textViewMensajeTrabajo =
            findViewById(R.id.textViewMensajeTrabajo)

        textViewObservacionTrabajo =
            findViewById(R.id.textViewObservacionTrabajo)

        editTextTrabajoRealizado =
            findViewById(R.id.editTextTrabajoRealizado)

        editTextDesperfectoTrabajo =
            findViewById(R.id.editTextDesperfectoTrabajo)

        autoCompleteMaterialTrabajo =
            findViewById(R.id.autoCompleteMaterialTrabajo)

        editTextCantidadMaterialTrabajo =
            findViewById(R.id.editTextCantidadMaterialTrabajo)

        buttonAgregarMaterialTrabajo =
            findViewById(R.id.buttonAgregarMaterialTrabajo)

        buttonFinalizarTrabajo =
            findViewById(R.id.buttonFinalizarTrabajo)

        recyclerViewMaterialesTrabajo =
            findViewById(R.id.recyclerViewMaterialesTrabajo)
    }

    private fun mostrarDatosAviso() {

        val direccion = intent.getStringExtra("AVISO_DIRECCION")
        val fecha = intent.getStringExtra("AVISO_FECHA")
        val hora = intent.getStringExtra("AVISO_HORA")
        val cliente = intent.getStringExtra("AVISO_CLIENTE")
        val telefono = intent.getStringExtra("AVISO_TELEFONO")
        val urgencia = intent.getStringExtra("AVISO_URGENCIA")
        val mensaje = intent.getStringExtra("AVISO_MENSAJE")
        val observacion = intent.getStringExtra("AVISO_OBSERVACION")

        textViewDireccionTrabajo.text =
            "Dirección: ${direccion ?: "Sin dirección"}"

        textViewFechaTrabajo.text =
            "Fecha de visita: ${fecha ?: "Sin fecha"} ${hora ?: ""}"

        textViewClienteTrabajo.text =
            "Cliente: ${cliente ?: "Sin cliente"}"

        textViewTelefonoTrabajo.text =
            "Teléfono: ${telefono ?: "Sin teléfono"}"

        textViewUrgenciaTrabajo.text =
            "Prioridad: ${urgencia ?: "Sin prioridad"}"

        textViewMensajeTrabajo.text =
            mensaje?.takeIf { it.isNotBlank() } ?: "Sin mensaje"

        textViewObservacionTrabajo.text =
            observacion?.takeIf { it.isNotBlank() } ?: "Sin observación"
        if (avisoFinalizado) {
            val nombreOperario =
                intent.getStringExtra("AVISO_OPERARIO")

            if (!nombreOperario.isNullOrBlank()) {
                textViewOperarioTrabajo.text =
                    "Operario responsable: $nombreOperario"
                textViewOperarioTrabajo.visibility = View.VISIBLE
            } else {
                textViewOperarioTrabajo.visibility = View.GONE
            }
        } else {
            textViewOperarioTrabajo.visibility = View.GONE
        }
    }

    private fun configurarMateriales() {

        materialTrabajoAdapter = MaterialTrabajoAdapter(
            mutableListOf(),
            onEliminarMaterial = { materialSeleccionado ->
                materialTrabajoAdapter.eliminarMaterial(
                    materialSeleccionado
                )
            },
            soloLectura = avisoFinalizado
        )

        recyclerViewMaterialesTrabajo.layoutManager =
            LinearLayoutManager(this)

        recyclerViewMaterialesTrabajo.adapter =
            materialTrabajoAdapter
    }

    private fun cargarMateriales(pagina: Int = 1) {

        RetrofitClient.api
            .obtenerMateriales(pagina)
            .enqueue(object : Callback<MaterialesResponse> {

                override fun onResponse(
                    call: Call<MaterialesResponse>,
                    response: Response<MaterialesResponse>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        val respuesta = response.body()!!

                        materialesDisponibles.addAll(respuesta.data)

                        if (
                            respuesta.meta.current_page <
                            respuesta.meta.last_page
                        ) {
                            cargarMateriales(
                                respuesta.meta.current_page + 1
                            )
                        } else {
                            configurarAutoCompleteMateriales()
                        }

                    } else {

                        Toast.makeText(
                            this@TrabajoActivity,
                            "No se pudieron cargar los materiales",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<MaterialesResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@TrabajoActivity,
                        "Error al cargar materiales: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
    private fun configurarAutoCompleteMateriales() {

        val nombresMateriales =
            materialesDisponibles.map { it.nombre }

        val adapter = ArrayAdapter(
            this,
            R.layout.item_opcion_aviso,
            R.id.textViewNombreOpcionAviso,
            nombresMateriales
        )

        autoCompleteMaterialTrabajo.setAdapter(adapter)
    }
    private fun configurarSeleccionMaterial() {

        autoCompleteMaterialTrabajo.setOnFocusChangeListener { _, tieneFoco ->

            if (tieneFoco) {
                materialSeleccionado = null
            }
        }

        autoCompleteMaterialTrabajo.setOnClickListener {
            materialSeleccionado = null
        }

        autoCompleteMaterialTrabajo.setOnItemClickListener { _, _, position, _ ->

            val adapter =
                autoCompleteMaterialTrabajo.adapter as ArrayAdapter<String>

            val nombreSeleccionado =
                adapter.getItem(position)

            materialSeleccionado =
                materialesDisponibles.find {
                    it.nombre == nombreSeleccionado
                }
        }
    }

    private fun configurarBotonAgregarMaterial() {

        buttonAgregarMaterialTrabajo.setOnClickListener {

            val material = materialSeleccionado

            if (material == null) {
                Toast.makeText(
                    this,
                    "Seleccioná un material",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val cantidadTexto =
                editTextCantidadMaterialTrabajo.text
                    .toString()
                    .trim()

            if (cantidadTexto.isEmpty()) {
                editTextCantidadMaterialTrabajo.error =
                    "Ingresá una cantidad"

                return@setOnClickListener
            }

            val cantidad = cantidadTexto.toIntOrNull()

            if (cantidad == null || cantidad < 1) {
                editTextCantidadMaterialTrabajo.error =
                    "La cantidad debe ser mayor a 0"

                return@setOnClickListener
            }

            val materialYaAgregado =
                materialTrabajoAdapter
                    .obtenerMateriales()
                    .any {
                        it.material.id == material.id
                    }

            if (materialYaAgregado) {
                Toast.makeText(
                    this,
                    "Ese material ya fue agregado",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val nuevoMaterial = MaterialSeleccionado(
                material = material,
                cantidad = cantidad
            )

            materialTrabajoAdapter.agregarMaterial(nuevoMaterial)

            materialSeleccionado = null
            autoCompleteMaterialTrabajo.text.clear()
            editTextCantidadMaterialTrabajo.text.clear()
        }
    }

    private fun configurarBotonFinalizarTrabajo() {

        buttonFinalizarTrabajo.setOnClickListener {

            val trabajoRealizado =
                editTextTrabajoRealizado.text
                    .toString()
                    .trim()

            val desperfecto =
                editTextDesperfectoTrabajo.text
                    .toString()
                    .trim()

            if (trabajoRealizado.isEmpty()) {
                editTextTrabajoRealizado.error =
                    "Ingresá el trabajo realizado"

                return@setOnClickListener
            }

            if (trabajoRealizado.length > 255) {
                editTextTrabajoRealizado.error =
                    "Máximo 255 caracteres"

                return@setOnClickListener
            }

            if (desperfecto.isEmpty()) {
                editTextDesperfectoTrabajo.error =
                    "Ingresá el desperfecto"

                return@setOnClickListener
            }

            if (desperfecto.length > 255) {
                editTextDesperfectoTrabajo.error =
                    "Máximo 255 caracteres"

                return@setOnClickListener
            }

            if (avisoId == -1) {
                Toast.makeText(
                    this,
                    "No se encontró el aviso",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val materialesSeleccionados =
                materialTrabajoAdapter.obtenerMateriales()

            if (materialesSeleccionados.isEmpty()) {
                Toast.makeText(
                    this,
                    "Agregá al menos un material",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val materialesRequest =
                materialesSeleccionados.map {
                    MaterialTrabajo(
                        material_id = it.material.id,
                        cantidad = it.cantidad
                    )
                }

            val request = TrabajoRequest(
                trabajo_realizado = trabajoRealizado,
                desperfecto = desperfecto,
                aviso_id = avisoId,
                materiales = materialesRequest
            )

            enviarTrabajo(request)
        }
    }

    private fun enviarTrabajo(request: TrabajoRequest) {

        buttonFinalizarTrabajo.isEnabled = false

        RetrofitClient.api
            .crearTrabajo(request)
            .enqueue(object : Callback<TrabajoResponse> {

                override fun onResponse(
                    call: Call<TrabajoResponse>,
                    response: Response<TrabajoResponse>
                ) {

                    buttonFinalizarTrabajo.isEnabled = true

                    if (response.isSuccessful && response.body() != null) {

                        val respuesta = response.body()!!

                        Toast.makeText(
                            this@TrabajoActivity,
                            respuesta.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        val mensajeError =
                            response.errorBody()?.string()
                                ?: "No se pudo finalizar el trabajo"

                        Toast.makeText(
                            this@TrabajoActivity,
                            mensajeError,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<TrabajoResponse>,
                    t: Throwable
                ) {

                    buttonFinalizarTrabajo.isEnabled = true

                    Toast.makeText(
                        this@TrabajoActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun mostrarTrabajoFinalizado() {

        val trabajoJson =
            intent.getStringExtra("AVISO_TRABAJO_JSON")

        if (trabajoJson.isNullOrBlank()) {
            Toast.makeText(
                this,
                "No se recibieron los detalles del trabajo finalizado",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        try {

            val trabajo = Gson().fromJson(
                trabajoJson,
                Trabajo::class.java
            )

            editTextTrabajoRealizado.setText(
                trabajo.trabajo_realizado
            )

            editTextDesperfectoTrabajo.setText(
                trabajo.desperfecto
            )

            val materialesUtilizados =
                trabajo.materiales.map { materialResponse ->

                    MaterialSeleccionado(
                        material = Material(
                            id = materialResponse.id,
                            nombre = materialResponse.nombre,
                            detalle = materialResponse.detalle ?: ""
                        ),
                        cantidad = materialResponse.cantidad
                    )
                }

            materialTrabajoAdapter.mostrarMateriales(
                materialesUtilizados
            )

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "No se pudieron mostrar los detalles del trabajo",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun configurarModoSoloLectura() {
        editTextTrabajoRealizado.isEnabled = false
        editTextDesperfectoTrabajo.isEnabled = false

        // Ocultar las indicaciones pensadas para completar el formulario
        findViewById<TextView>(R.id.textViewSubtituloTrabajo)
            .visibility = View.GONE

        findViewById<TextView>(R.id.textViewAyudaRegistroTrabajo)
            .visibility = View.GONE

        findViewById<TextView>(R.id.textViewAyudaMaterialesTrabajo)
            .visibility = View.GONE

        // Ocultar los controles para agregar o finalizar el trabajo
        autoCompleteMaterialTrabajo.visibility = View.GONE
        editTextCantidadMaterialTrabajo.visibility = View.GONE
        buttonAgregarMaterialTrabajo.visibility = View.GONE
        buttonFinalizarTrabajo.visibility = View.GONE
    }
}