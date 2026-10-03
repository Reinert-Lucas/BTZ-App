package com.ivanbm.frontend_btz

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.adapter.MaterialTrabajoAdapter
import com.ivanbm.frontend_btz.model.Material
import com.ivanbm.frontend_btz.model.MaterialSeleccionado
import com.ivanbm.frontend_btz.model.MaterialTrabajo
import com.ivanbm.frontend_btz.model.MaterialesResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import com.ivanbm.frontend_btz.model.TrabajoRequest
import com.ivanbm.frontend_btz.model.TrabajoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.emptyList

class TrabajoActivity : AppCompatActivity() {

    private lateinit var editTextTrabajoRealizado: EditText

    private lateinit var editTextDesperfectoTrabajo: EditText
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
    private var materialesDisponibles = mutableListOf<Material>()
    private var materialSeleccionado: Material? = null
    private var avisoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_trabajo)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        inicializarVistas()
        avisoId = intent.getIntExtra("AVISO_ID", -1)
        mostrarDatosAviso()
        configurarMateriales()
        cargarMateriales()
        configurarSeleccionMaterial()
        configurarBotonAgregarMaterial()
        configurarBotonFinalizarTrabajo()
    }

    private fun inicializarVistas() {

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

        val direccion =
            intent.getStringExtra("AVISO_DIRECCION")

        val fecha =
            intent.getStringExtra("AVISO_FECHA")

        val hora =
            intent.getStringExtra("AVISO_HORA")

        val cliente =
            intent.getStringExtra("AVISO_CLIENTE")

        val telefono =
            intent.getStringExtra("AVISO_TELEFONO")

        val urgencia =
            intent.getStringExtra("AVISO_URGENCIA")

        val mensaje =
            intent.getStringExtra("AVISO_MENSAJE")

        val observacion =
            intent.getStringExtra("AVISO_OBSERVACION")

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
            "Mensaje: ${mensaje ?: "Sin mensaje"}"

        textViewObservacionTrabajo.text =
            "Observación: ${observacion ?: "Sin observación"}"
    }
    private fun configurarMateriales() {

        materialTrabajoAdapter = MaterialTrabajoAdapter(
            mutableListOf()
        ) { materialSeleccionado ->

            materialTrabajoAdapter.eliminarMaterial(
                materialSeleccionado
            )
        }

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

                        materialesDisponibles.addAll(
                            respuesta.data
                        )

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
            android.R.layout.simple_dropdown_item_1line,
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

            val cantidad =
                cantidadTexto.toIntOrNull()

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

            val nuevoMaterial =
                MaterialSeleccionado(
                    material = material,
                    cantidad = cantidad
                )

            materialTrabajoAdapter.agregarMaterial(
                nuevoMaterial
            )

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

            val request =
                TrabajoRequest(
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
}