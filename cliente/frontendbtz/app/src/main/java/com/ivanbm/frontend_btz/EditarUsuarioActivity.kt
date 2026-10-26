package com.ivanbm.frontend_btz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ivanbm.frontend_btz.model.UsuarioRequest
import com.ivanbm.frontend_btz.model.UsuarioResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
class EditarUsuarioActivity : AppCompatActivity() {

    private var usuarioId: Int = -1
    private lateinit var editTextNombreUsuario: EditText
    private lateinit var editTextDniUsuario: EditText
    private lateinit var editTextTelefonoUsuario: EditText
    private lateinit var editTextPasswordUsuario: EditText
    private lateinit var spinnerRolUsuario: Spinner
    private lateinit var buttonGuardarCambiosUsuario: Button
    private lateinit var buttonEliminarUsuario: Button
    private lateinit var progressBarEditarUsuario: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_usuario)

        val scrollViewEditarUsuario =
            findViewById<androidx.core.widget.NestedScrollView>(
                R.id.scrollViewEditarUsuario
            )

        val paddingSuperiorInicial = scrollViewEditarUsuario.paddingTop
        val paddingInferiorInicial = scrollViewEditarUsuario.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(scrollViewEditarUsuario) { vista, insets ->
            val barrasSistema = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )

            vista.updatePadding(
                top = paddingSuperiorInicial + barrasSistema.top,
                bottom = paddingInferiorInicial + barrasSistema.bottom
            )

            insets
        }
        usuarioId = intent.getIntExtra(
            "USUARIO_ID",
            -1
        )

        if (usuarioId == -1) {

            Toast.makeText(
                this,
                "No se pudo obtener el usuario",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        inicializarVistas()
        configurarSpinnerRol()

        cargarUsuario()

        buttonGuardarCambiosUsuario.setOnClickListener {
            actualizarUsuario()
        }

        buttonEliminarUsuario.setOnClickListener {
            mostrarConfirmacionEliminar()
        }
        findViewById<android.view.View>(R.id.buttonVolverEditarUsuario)
            .setOnClickListener {
                finish()
            }
    }

    private fun inicializarVistas() {

        editTextNombreUsuario =
            findViewById(R.id.editTextNombreUsuario)

        editTextDniUsuario =
            findViewById(R.id.editTextDniUsuario)

        editTextTelefonoUsuario =
            findViewById(R.id.editTextTelefonoUsuario)

        editTextPasswordUsuario =
            findViewById(R.id.editTextPasswordUsuario)

        spinnerRolUsuario =
            findViewById(R.id.spinnerRolUsuario)

        buttonGuardarCambiosUsuario =
            findViewById(R.id.buttonGuardarCambiosUsuario)

        buttonEliminarUsuario =
            findViewById(R.id.buttonEliminarUsuario)

        progressBarEditarUsuario =
            findViewById(R.id.progressBarEditarUsuario)
    }
    private fun configurarSpinnerRol() {
        val roles = listOf("admin", "operario")

        fun obtenerIconoRol(rol: String): Int {
            return when (rol) {
                "admin" -> R.drawable.ic_usuario_administrador
                "operario" -> R.drawable.ic_usuario_operario
                else -> R.drawable.ic_usuario
            }
        }

        val adaptadorRoles = object : ArrayAdapter<String>(
            this,
            R.layout.item_rol_seleccionado,
            R.id.textViewRolSeleccionado,
            roles
        ) {
            private fun configurarVista(
                vista: View,
                rol: String,
                esOpcion: Boolean
            ): View {
                val imageView = vista.findViewById<ImageView>(
                    if (esOpcion) {
                        R.id.imageViewIconoOpcionRol
                    } else {
                        R.id.imageViewIconoRolSeleccionado
                    }
                )

                val textView = vista.findViewById<TextView>(
                    if (esOpcion) {
                        R.id.textViewOpcionRol
                    } else {
                        R.id.textViewRolSeleccionado
                    }
                )

                imageView.setImageResource(obtenerIconoRol(rol))

                textView.text = when (rol) {
                    "admin" -> "Administrador"
                    "operario" -> "Operario"
                    else -> rol
                }

                return vista
            }

            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val vista = convertView ?: layoutInflater.inflate(
                    R.layout.item_rol_seleccionado,
                    parent,
                    false
                )

                return configurarVista(
                    vista,
                    getItem(position) ?: "",
                    false
                )
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val vista = convertView ?: layoutInflater.inflate(
                    R.layout.item_rol_opcion,
                    parent,
                    false
                )

                return configurarVista(
                    vista,
                    getItem(position) ?: "",
                    true
                )
            }
        }

        spinnerRolUsuario.adapter = adaptadorRoles
    }
    private fun cargarUsuario() {

        RetrofitClient.api.obtenerUsuario(usuarioId)
            .enqueue(object : Callback<UsuarioResponse> {

                override fun onResponse(
                    call: Call<UsuarioResponse>,
                    response: Response<UsuarioResponse>
                ) {

                    if (!response.isSuccessful) {

                        Toast.makeText(
                            this@EditarUsuarioActivity,
                            "Error HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    val respuesta = response.body()

                    if (respuesta != null && respuesta.status) {

                        val usuario = respuesta.data

                        android.util.Log.d(
                            "EDITAR_USUARIO",
                            "Usuario obtenido: $usuario"
                        )

                        editTextNombreUsuario.setText(
                            usuario.nombre
                        )

                        editTextDniUsuario.setText(
                            usuario.dni
                        )

                        editTextTelefonoUsuario.setText(
                            usuario.telefono
                        )


                        val posicionRol =
                            if (usuario.rol == "operario") 1 else 0

                        spinnerRolUsuario.setSelection(
                            posicionRol
                        )

                    } else {

                        Toast.makeText(
                            this@EditarUsuarioActivity,
                            "No se pudo obtener el usuario",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<UsuarioResponse>,
                    t: Throwable
                ) {

                    android.util.Log.e(
                        "EDITAR_USUARIO",
                        "Error de conexión",
                        t
                    )

                    Toast.makeText(
                        this@EditarUsuarioActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun actualizarUsuario() {

        val nombre =
            editTextNombreUsuario.text.toString().trim()

        val dni =
            editTextDniUsuario.text.toString().trim()

        val telefono =
            editTextTelefonoUsuario.text.toString().trim()

        val password =
            editTextPasswordUsuario.text.toString()

        val rol =
            spinnerRolUsuario.selectedItem.toString()


        if (nombre.isEmpty()) {
            editTextNombreUsuario.error =
                "Ingrese el nombre"
            return
        }

        if (dni.isEmpty()) {
            editTextDniUsuario.error =
                "Ingrese el DNI"
            return
        }

        if (telefono.isEmpty()) {
            editTextTelefonoUsuario.error =
                "Ingrese el teléfono"
            return
        }

        if (password.isNotEmpty() && password.length < 8) {
            editTextPasswordUsuario.error =
                "La contraseña debe tener al menos 8 caracteres"
            return
        }

        val usuarioRequest = UsuarioRequest(
            nombre = nombre,
            password = if (password.isEmpty()) null else password,
            rol = rol,
            dni = dni,
            telefono = telefono,
        )

        mostrarCargando(true)

        RetrofitClient.api.actualizarUsuario(
            usuarioId,
            usuarioRequest
        ).enqueue(object : Callback<UsuarioResponse> {

            override fun onResponse(
                call: Call<UsuarioResponse>,
                response: Response<UsuarioResponse>
            ) {

                mostrarCargando(false)

                if (!response.isSuccessful) {

                    val error =
                        response.errorBody()?.string()

                    android.util.Log.e(
                        "EDITAR_USUARIO",
                        "Código HTTP: ${response.code()}"
                    )

                    android.util.Log.e(
                        "EDITAR_USUARIO",
                        error ?: "Sin información del error"
                    )

                    Toast.makeText(
                        this@EditarUsuarioActivity,
                        "Error HTTP ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()

                    return
                }

                val respuesta = response.body()

                android.util.Log.d(
                    "EDITAR_USUARIO",
                    "Respuesta actualización: $respuesta"
                )

                if (respuesta != null && respuesta.status) {

                    Toast.makeText(
                        this@EditarUsuarioActivity,
                        respuesta.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this@EditarUsuarioActivity,
                        "No se pudo actualizar el usuario",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<UsuarioResponse>,
                t: Throwable
            ) {

                mostrarCargando(false)

                android.util.Log.e(
                    "EDITAR_USUARIO",
                    "Error de conexión",
                    t
                )

                Toast.makeText(
                    this@EditarUsuarioActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun mostrarConfirmacionEliminar() {

        AlertDialog.Builder(this)
            .setTitle("Eliminar usuario")
            .setMessage(
                "¿Está seguro de que desea eliminar este usuario?"
            )
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarUsuario()
            }
            .show()
    }

    private fun eliminarUsuario() {

        mostrarCargando(true)

        RetrofitClient.api.eliminarUsuario(usuarioId)
            .enqueue(object : Callback<Void> {

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {

                    mostrarCargando(false)


                    if (!response.isSuccessful) {
                        val error = response.errorBody()?.string()

                        android.util.Log.e(
                            "EDITAR_USUARIO",
                            "HTTP ${response.code()}: $error"
                        )

                        if (response.code() == 422) {
                            mostrarErrorValidacion(error)
                        } else {
                            Toast.makeText(
                                this@EditarUsuarioActivity,
                                "No se pudo actualizar el usuario (HTTP ${response.code()})",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        return
                    }

                    Toast.makeText(
                        this@EditarUsuarioActivity,
                        "Usuario eliminado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }

                override fun onFailure(
                    call: Call<Void>,
                    t: Throwable
                ) {

                    mostrarCargando(false)

                    android.util.Log.e(
                        "EDITAR_USUARIO",
                        "Error al eliminar usuario",
                        t
                    )

                    Toast.makeText(
                        this@EditarUsuarioActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun mostrarCargando(cargando: Boolean) {

        if (cargando) {

            progressBarEditarUsuario.visibility =
                ProgressBar.VISIBLE

            buttonGuardarCambiosUsuario.isEnabled = false
            buttonEliminarUsuario.isEnabled = false

        } else {

            progressBarEditarUsuario.visibility =
                ProgressBar.GONE

            buttonGuardarCambiosUsuario.isEnabled = true
            buttonEliminarUsuario.isEnabled = true
        }
    }

    private fun mostrarErrorValidacion(errorJson: String?) {
        if (errorJson.isNullOrBlank()) {
            Toast.makeText(
                this,
                "No se pudieron validar los datos ingresados.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            val json = JSONObject(errorJson)
            val errores = json.optJSONObject("errors")

            if (errores == null || errores.length() == 0) {
                Toast.makeText(
                    this,
                    json.optString("message", "No se pudo actualizar el usuario"),
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            val campos = mapOf(
                "nombre" to editTextNombreUsuario,
                "dni" to editTextDniUsuario,
                "telefono" to editTextTelefonoUsuario,
                "password" to editTextPasswordUsuario
            )

            val nombresCampos = mapOf(
                "nombre" to "Nombre",
                "dni" to "DNI",
                "telefono" to "Teléfono",
                "password" to "Contraseña",
                "rol" to "Rol"
            )

            var primerMensaje: String? = null
            val claves = errores.keys()

            while (claves.hasNext()) {
                val campo = claves.next()
                val mensajeServidor = errores.optJSONArray(campo)
                    ?.optString(0)
                    .orEmpty()

                val mensaje = when {
                    campo == "dni" &&
                            mensajeServidor.contains("taken", true) ->
                        "El DNI ingresado ya existe"

                    campo == "password" &&
                            mensajeServidor.contains("at least", true) ->
                        "La contraseña debe tener al menos 8 caracteres"

                    campo == "password" &&
                            mensajeServidor.contains("required", true) ->
                        "La contraseña es obligatoria"

                    else -> {
                        val nombreCampo = nombresCampos[campo] ?: campo
                        "$nombreCampo: ${mensajeServidor.ifBlank { "Dato inválido" }}"
                    }
                }

                campos[campo]?.error = mensaje

                if (primerMensaje == null) {
                    primerMensaje = mensaje
                }
            }

            Toast.makeText(
                this,
                primerMensaje ?: "Revisá los datos ingresados",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            android.util.Log.e(
                "EDITAR_USUARIO",
                "Error al interpretar validaciones",
                e
            )

            Toast.makeText(
                this,
                "No se pudieron validar los datos ingresados.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}