package com.ivanbm.frontend_btz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ivanbm.frontend_btz.model.ClienteRequest
import com.ivanbm.frontend_btz.model.ClienteResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearClienteActivity : AppCompatActivity() {

    private lateinit var editTextNombreCliente: EditText
    private lateinit var editTextEmailCliente: EditText
    private lateinit var switchAseguradoCliente: Switch
    private lateinit var editTextDetalleAseguradoCliente: EditText
    private lateinit var buttonGuardarCliente: Button
    private lateinit var progressBarGuardarCliente: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_cliente)

        editTextNombreCliente =
            findViewById(R.id.editTextNombreCliente)

        editTextEmailCliente =
            findViewById(R.id.editTextEmailCliente)

        switchAseguradoCliente =
            findViewById(R.id.switchAseguradoCliente)

        editTextDetalleAseguradoCliente =
            findViewById(R.id.editTextDetalleAseguradoCliente)

        buttonGuardarCliente =
            findViewById(R.id.buttonGuardarCliente)

        progressBarGuardarCliente =
            findViewById(R.id.progressBarGuardarCliente)

        configurarSwitchAsegurado()

        buttonGuardarCliente.setOnClickListener {
            crearCliente()
        }
    }

    private fun configurarSwitchAsegurado() {

        switchAseguradoCliente.setOnCheckedChangeListener { _, estaAsegurado ->

            editTextDetalleAseguradoCliente.isEnabled = estaAsegurado

            if (!estaAsegurado) {
                editTextDetalleAseguradoCliente.text.clear()
            }
        }
    }

    private fun crearCliente() {

        val nombre = editTextNombreCliente.text.toString().trim()
        val email = editTextEmailCliente.text.toString().trim()
        val estaAsegurado = switchAseguradoCliente.isChecked
        val detalleAsegurado =
            editTextDetalleAseguradoCliente.text.toString().trim()

        if (nombre.isEmpty()) {

            editTextNombreCliente.error = "Ingrese el nombre"
            editTextNombreCliente.requestFocus()
            return
        }

        if (email.isEmpty()) {

            editTextEmailCliente.error = "Ingrese el email"
            editTextEmailCliente.requestFocus()
            return
        }

        if (estaAsegurado && detalleAsegurado.isEmpty()) {

            editTextDetalleAseguradoCliente.error =
                "Ingrese el detalle del asegurado"

            editTextDetalleAseguradoCliente.requestFocus()
            return
        }

        val request = ClienteRequest(
            nombre = nombre,
            email = email,
            asegurado = estaAsegurado,
            asegurado_detalle =
                if (estaAsegurado) detalleAsegurado else null
        )

        mostrarCargando(true)

        RetrofitClient.api.crearCliente(request)
            .enqueue(object : Callback<ClienteResponse> {

                override fun onResponse(
                    call: Call<ClienteResponse>,
                    response: Response<ClienteResponse>
                ) {

                    mostrarCargando(false)

                    if (!response.isSuccessful) {

                        val error =
                            response.errorBody()?.string()

                        android.util.Log.e(
                            "CREAR_CLIENTE",
                            "Código HTTP: ${response.code()}"
                        )

                        android.util.Log.e(
                            "CREAR_CLIENTE",
                            error ?: "Sin información del error"
                        )

                        Toast.makeText(
                            this@CrearClienteActivity,
                            "Error HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    val respuesta = response.body()

                    android.util.Log.d(
                        "CREAR_CLIENTE",
                        "Respuesta: $respuesta"
                    )

                    if (respuesta != null && respuesta.status) {

                        Toast.makeText(
                            this@CrearClienteActivity,
                            respuesta.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()

                    } else {

                        Toast.makeText(
                            this@CrearClienteActivity,
                            respuesta?.message
                                ?: "No se pudo crear el cliente",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ClienteResponse>,
                    t: Throwable
                ) {
                    mostrarCargando(false)

                    android.util.Log.e(
                        "CREAR_CLIENTE",
                        "Error de conexión",
                        t
                    )

                    Toast.makeText(
                        this@CrearClienteActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun mostrarCargando(cargando: Boolean) {

        if (cargando) {

            progressBarGuardarCliente.visibility =
                ProgressBar.VISIBLE

            buttonGuardarCliente.isEnabled = false

        } else {

            progressBarGuardarCliente.visibility =
                ProgressBar.GONE

            buttonGuardarCliente.isEnabled = true
        }
    }
}