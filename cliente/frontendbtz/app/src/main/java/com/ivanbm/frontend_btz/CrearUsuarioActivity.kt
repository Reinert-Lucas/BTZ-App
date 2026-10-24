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
import com.ivanbm.frontend_btz.model.UsuarioRequest
import com.ivanbm.frontend_btz.model.UsuarioResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
class CrearUsuarioActivity : AppCompatActivity() {

    private lateinit var editTextNombreUsuario: EditText
    private lateinit var editTextDniUsuario: EditText
    private lateinit var editTextTelefonoUsuario: EditText
    private lateinit var editTextPasswordUsuario: EditText
    private lateinit var spinnerRolUsuario: Spinner
    private lateinit var buttonGuardarUsuario: Button
    private lateinit var progressBarGuardarUsuario: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_usuario)
        findViewById<ImageButton>(R.id.buttonVolverCrearUsuario)
            .setOnClickListener {
                finish()
            }
        val scrollViewCrearUsuario =
            findViewById<View>(R.id.scrollViewCrearUsuario)

        val paddingSuperiorInicial = scrollViewCrearUsuario.paddingTop
        val paddingInferiorInicial = scrollViewCrearUsuario.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(scrollViewCrearUsuario) { vista, insets ->

            val barrasSistema = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )

            vista.setPadding(
                vista.paddingLeft,
                paddingSuperiorInicial + barrasSistema.top,
                vista.paddingRight,
                paddingInferiorInicial + barrasSistema.bottom
            )

            insets
        }
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

        buttonGuardarUsuario =
            findViewById(R.id.buttonGuardarUsuario)

        progressBarGuardarUsuario =
            findViewById(R.id.progressBarGuardarUsuario)

        buttonGuardarUsuario.setOnClickListener {
            crearUsuario()
        }
        configurarSpinnerRol()
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
                parent: android.view.ViewGroup
            ): View {
                val vista = convertView ?: layoutInflater.inflate(
                    R.layout.item_rol_seleccionado,
                    parent,
                    false
                )

                return configurarVista(vista, getItem(position) ?: "", false)
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: android.view.ViewGroup
            ): View {
                val vista = convertView ?: layoutInflater.inflate(
                    R.layout.item_rol_opcion,
                    parent,
                    false
                )

                return configurarVista(vista, getItem(position) ?: "", true)
            }
        }

        spinnerRolUsuario.adapter = adaptadorRoles
    }


    private fun crearUsuario() {

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

        if (password.isEmpty()) {
            editTextPasswordUsuario.error =
                "Ingrese la contraseña"
            return
        }

        if (password.length < 8) {
            editTextPasswordUsuario.error =
                "La contraseña debe tener al menos 8 caracteres"
            return
        }

        val usuarioRequest = UsuarioRequest(
            nombre = nombre,
            password = password,
            rol = rol,
            dni = dni,
            telefono = telefono,
        )

        mostrarCargando(true)

        RetrofitClient.api.crearUsuario(
            usuarioRequest
        ).enqueue(object : Callback<UsuarioResponse> {

            override fun onResponse(
                call: Call<UsuarioResponse>,
                response: Response<UsuarioResponse>
            ) {

                mostrarCargando(false)


                if (!response.isSuccessful) {
                    val error = response.errorBody()?.string()

                    android.util.Log.e(
                        "CREAR_USUARIO",
                        "Código HTTP: ${response.code()} - $error"
                    )

                    if (response.code() == 422) {
                        mostrarErrorValidacion(error)
                    } else {
                        Toast.makeText(
                            this@CrearUsuarioActivity,
                            "No se pudo crear el usuario (HTTP ${response.code()})",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    return
                }

                val respuesta = response.body()

                android.util.Log.d(
                    "CREAR_USUARIO",
                    "Respuesta completa: $respuesta"
                )

                if (respuesta != null && respuesta.status) {

                    android.util.Log.d(
                        "CREAR_USUARIO",
                        "Usuario creado: ${respuesta.data}"
                    )

                    Toast.makeText(
                        this@CrearUsuarioActivity,
                        respuesta.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this@CrearUsuarioActivity,
                        "No se pudo crear el usuario",
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
                    "CREAR_USUARIO",
                    "Error de conexión",
                    t
                )

                Toast.makeText(
                    this@CrearUsuarioActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun mostrarCargando(cargando: Boolean) {

        if (cargando) {

            progressBarGuardarUsuario.visibility =
                ProgressBar.VISIBLE

            buttonGuardarUsuario.isEnabled = false

        } else {

            progressBarGuardarUsuario.visibility =
                ProgressBar.GONE

            buttonGuardarUsuario.isEnabled = true
        }
    }

    private fun mostrarErrorValidacion(errorJson: String?) {
        if (errorJson.isNullOrBlank()) {
            Toast.makeText(
                this,
                "No se pudo crear el usuario. Revisá los datos ingresados.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            val json = JSONObject(errorJson)
            val errores = json.optJSONObject("errors")

            if (errores != null && errores.length() > 0) {
                val campos = mapOf(
                    "nombre" to editTextNombreUsuario,
                    "dni" to editTextDniUsuario,
                    "telefono" to editTextTelefonoUsuario,
                    "password" to editTextPasswordUsuario
                )

                var primerMensaje: String? = null

                val nombresCampos = mapOf(
                    "nombre" to "Nombre",
                    "dni" to "DNI",
                    "telefono" to "Teléfono",
                    "password" to "Contraseña",
                    "rol" to "Rol"
                )

                val claves = errores.keys()

                while (claves.hasNext()) {
                    val campo = claves.next()
                    val mensajes = errores.optJSONArray(campo)
                    val mensajeServidor = mensajes?.optString(0).orEmpty()

                    val mensaje = when {
                        campo == "dni" &&
                                mensajeServidor.contains("taken", ignoreCase = true) ->
                            "El DNI ingresado ya existe"

                        campo == "password" &&
                                mensajeServidor.contains("at least", ignoreCase = true) ->
                            "La contraseña debe tener al menos 8 caracteres"

                        campo == "password" &&
                                mensajeServidor.contains("required", ignoreCase = true) ->
                            "La contraseña es obligatoria"

                        campo == "nombre" &&
                                mensajeServidor.contains("required", ignoreCase = true) ->
                            "El nombre es obligatorio"

                        campo == "dni" &&
                                mensajeServidor.contains("required", ignoreCase = true) ->
                            "El DNI es obligatorio"

                        campo == "telefono" &&
                                mensajeServidor.contains("required", ignoreCase = true) ->
                            "El teléfono es obligatorio"

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

            } else {
                val mensaje = json.optString(
                    "message",
                    "No se pudo crear el usuario"
                )

                Toast.makeText(
                    this,
                    mensaje,
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "No se pudieron validar los datos. Revisá la información ingresada.",
                Toast.LENGTH_LONG
            ).show()

            android.util.Log.e("CREAR_USUARIO", "Error al interpretar validaciones", e)
        }
    }
}