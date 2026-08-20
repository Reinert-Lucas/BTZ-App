package com.ivanbm.frontend_btz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ivanbm.frontend_btz.model.ClienteRequest
import com.ivanbm.frontend_btz.model.ClienteResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarClienteActivity : AppCompatActivity() {

    private var clienteId: Int = -1
    private lateinit var editTextNombreCliente: EditText
    private lateinit var editTextEmailCliente: EditText
    private lateinit var switchAseguradoCliente: Switch
    private lateinit var editTextDetalleAseguradoCliente: EditText
    private lateinit var progressBarEditarCliente: ProgressBar
    private lateinit var buttonGuardarCambiosCliente: Button
    private lateinit var buttonEliminarCliente: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_cliente)

        editTextNombreCliente =
            findViewById(R.id.editTextNombreCliente)

        editTextEmailCliente =
            findViewById(R.id.editTextEmailCliente)

        switchAseguradoCliente =
            findViewById(R.id.switchAseguradoCliente)

        editTextDetalleAseguradoCliente =
            findViewById(R.id.editTextDetalleAseguradoCliente)

        progressBarEditarCliente =
            findViewById(R.id.progressBarEditarCliente)

        buttonGuardarCambiosCliente =
            findViewById(R.id.buttonGuardarCambiosCliente)

        buttonEliminarCliente =
            findViewById(R.id.buttonEliminarCliente)

        clienteId = intent.getIntExtra(
            "CLIENTE_ID",
            -1
        )

        val buttonGuardarCambiosCliente =
            findViewById<Button>(R.id.buttonGuardarCambiosCliente)

        val buttonEliminarCliente =
            findViewById<Button>(R.id.buttonEliminarCliente)

        buttonGuardarCambiosCliente.setOnClickListener {
            actualizarCliente()
        }
        buttonEliminarCliente.setOnClickListener {
            mostrarConfirmacionEliminar()
        }

        if (clienteId == -1) {

            Toast.makeText(
                this,
                "No se pudo obtener el cliente",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }
        configurarSwitchAsegurado()
        cargarCliente()
    }
    private fun actualizarCliente() {

        val nombre = editTextNombreCliente.text.toString().trim()
        val email = editTextEmailCliente.text.toString().trim()
        val asegurado = switchAseguradoCliente.isChecked
        val detalle = editTextDetalleAseguradoCliente.text.toString().trim()

        if (nombre.isEmpty()) {
            editTextNombreCliente.error = "Ingrese el nombre"
            return
        }

        if (email.isEmpty()) {
            editTextEmailCliente.error = "Ingrese el email"
            return
        }

        val clienteRequest = ClienteRequest(
            nombre = nombre,
            email = email,
            asegurado = asegurado,
            asegurado_detalle = if (asegurado) detalle else null
        )

        mostrarCargando(true)

        RetrofitClient.api.actualizarCliente(
            clienteId,
            clienteRequest
        ).enqueue(object : Callback<ClienteResponse> {

            override fun onResponse(
                call: Call<ClienteResponse>,
                response: Response<ClienteResponse>
            ) {
                if (!response.isSuccessful) {

                    Toast.makeText(
                        this@EditarClienteActivity,
                        "Error HTTP ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()

                    return
                }

                val respuesta = response.body()

                android.util.Log.d(
                    "EDITAR_CLIENTE",
                    "Respuesta actualización: $respuesta"
                )

                if (respuesta != null && respuesta.status) {

                    Toast.makeText(
                        this@EditarClienteActivity,
                        respuesta.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this@EditarClienteActivity,
                        "No se pudo actualizar el cliente",
                        Toast.LENGTH_LONG
                    ).show()
                }
                mostrarCargando(false)
            }

            override fun onFailure(
                call: Call<ClienteResponse>,
                t: Throwable
            ) {

                android.util.Log.e(
                    "EDITAR_CLIENTE",
                    "Error de conexión",
                    t
                )

                Toast.makeText(
                    this@EditarClienteActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                mostrarCargando(false)

            }
        })
    }
    private fun mostrarConfirmacionEliminar() {

        AlertDialog.Builder(this)
            .setTitle("Eliminar cliente")
            .setMessage(
                "¿Está seguro de que desea eliminar este cliente?"
            )
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarCliente()
            }
            .show()
    }
    private fun eliminarCliente() {
        mostrarCargando(true)
        RetrofitClient.api.eliminarCliente(clienteId)
            .enqueue(object : Callback<Void> {

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {

                    if (!response.isSuccessful) {

                        Toast.makeText(
                            this@EditarClienteActivity,
                            "Error HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    Toast.makeText(
                        this@EditarClienteActivity,
                        "Cliente eliminado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    mostrarCargando(false)
                    finish()
                }

                override fun onFailure(
                    call: Call<Void>,
                    t: Throwable
                ) {

                    android.util.Log.e(
                        "EDITAR_CLIENTE",
                        "Error al eliminar cliente",
                        t
                    )

                    Toast.makeText(
                        this@EditarClienteActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    mostrarCargando(false)
                }
            })
    }
    private fun configurarSwitchAsegurado() {

        switchAseguradoCliente.setOnCheckedChangeListener { _, estaAsegurado ->

            editTextDetalleAseguradoCliente.isEnabled =
                estaAsegurado

            if (!estaAsegurado) {
                editTextDetalleAseguradoCliente.text.clear()
            }
        }
    }
    private fun cargarCliente() {

        RetrofitClient.api.obtenerCliente(clienteId)
            .enqueue(object : Callback<ClienteResponse> {

                override fun onResponse(
                    call: Call<ClienteResponse>,
                    response: Response<ClienteResponse>
                ) {

                    if (!response.isSuccessful) {

                        Toast.makeText(
                            this@EditarClienteActivity,
                            "Error HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    val respuesta = response.body()

                    if (respuesta != null && respuesta.status) {

                        val cliente = respuesta.data

                        android.util.Log.d(
                            "EDITAR_CLIENTE",
                            "Cliente obtenido: $cliente"
                        )

                        editTextNombreCliente.setText(cliente.nombre)

                        editTextEmailCliente.setText(cliente.email)

                        switchAseguradoCliente.isChecked =
                            cliente.asegurado

                        editTextDetalleAseguradoCliente.setText(
                            cliente.asegurado_detalle ?: ""
                        )

                    } else {

                        Toast.makeText(
                            this@EditarClienteActivity,
                            "No se pudo obtener el cliente",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ClienteResponse>,
                    t: Throwable
                ) {

                    android.util.Log.e(
                        "EDITAR_CLIENTE",
                        "Error de conexión",
                        t
                    )

                    Toast.makeText(
                        this@EditarClienteActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
    private fun mostrarCargando(cargando: Boolean) {

        if (cargando) {

            progressBarEditarCliente.visibility =
                ProgressBar.VISIBLE

            buttonGuardarCambiosCliente.isEnabled = false
            buttonEliminarCliente.isEnabled = false

        } else {

            progressBarEditarCliente.visibility =
                ProgressBar.GONE

            buttonGuardarCambiosCliente.isEnabled = true
            buttonEliminarCliente.isEnabled = true
        }
    }
}