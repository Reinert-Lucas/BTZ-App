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

        configurarSpinnerRol()

        buttonGuardarUsuario.setOnClickListener {
            crearUsuario()
        }
    }

    private fun configurarSpinnerRol() {

        val roles = listOf(
            "admin",
            "operario"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roles
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerRolUsuario.adapter = adapter
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

                    val error =
                        response.errorBody()?.string()

                    android.util.Log.e(
                        "CREAR_USUARIO",
                        "Código HTTP: ${response.code()}"
                    )

                    android.util.Log.e(
                        "CREAR_USUARIO",
                        error ?: "Sin información del error"
                    )

                    Toast.makeText(
                        this@CrearUsuarioActivity,
                        "Error HTTP ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()

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
}