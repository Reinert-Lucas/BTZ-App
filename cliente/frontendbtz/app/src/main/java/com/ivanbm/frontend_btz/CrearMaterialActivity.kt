package com.ivanbm.frontend_btz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ivanbm.frontend_btz.model.MaterialRequest
import com.ivanbm.frontend_btz.model.MaterialResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearMaterialActivity : AppCompatActivity() {

    private lateinit var editTextNombreMaterial: EditText
    private lateinit var editTextDetalleMaterial: EditText
    private lateinit var buttonGuardarMaterial: Button
    private lateinit var progressBarGuardarMaterial: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_material)

        editTextNombreMaterial =
            findViewById(R.id.editTextNombreMaterial)

        editTextDetalleMaterial =
            findViewById(R.id.editTextDetalleMaterial)

        buttonGuardarMaterial =
            findViewById(R.id.buttonGuardarMaterial)

        progressBarGuardarMaterial =
            findViewById(R.id.progressBarGuardarMaterial)

        buttonGuardarMaterial.setOnClickListener {
            crearMaterial()
        }
    }

    private fun crearMaterial() {

        val nombre =
            editTextNombreMaterial.text
                .toString()
                .trim()

        val detalle =
            editTextDetalleMaterial.text
                .toString()
                .trim()

        if (nombre.isEmpty()) {
            editTextNombreMaterial.error =
                "Ingrese el nombre del material"
            return
        }

        if (nombre.length > 50) {
            editTextNombreMaterial.error =
                "El nombre no puede superar los 50 caracteres"
            return
        }

        if (detalle.isEmpty()) {
            editTextDetalleMaterial.error =
                "Ingrese el detalle del material"
            return
        }

        if (detalle.length > 100) {
            editTextDetalleMaterial.error =
                "El detalle no puede superar los 100 caracteres"
            return
        }

        val request = MaterialRequest(
            nombre = nombre,
            detalle = detalle
        )

        progressBarGuardarMaterial.visibility =
            ProgressBar.VISIBLE

        buttonGuardarMaterial.isEnabled = false

        RetrofitClient.api
            .crearMaterial(request)
            .enqueue(
                object : Callback<MaterialResponse> {

                    override fun onResponse(
                        call: Call<MaterialResponse>,
                        response: Response<MaterialResponse>
                    ) {

                        progressBarGuardarMaterial.visibility =
                            ProgressBar.GONE

                        buttonGuardarMaterial.isEnabled =
                            true

                        if (!response.isSuccessful) {

                            Toast.makeText(
                                this@CrearMaterialActivity,
                                "Error HTTP ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()

                            return
                        }

                        val respuesta = response.body()

                        if (
                            respuesta != null &&
                            respuesta.status
                        ) {

                            Toast.makeText(
                                this@CrearMaterialActivity,
                                "Material creado con éxito",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {

                            Toast.makeText(
                                this@CrearMaterialActivity,
                                "No se pudo crear el material",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<MaterialResponse>,
                        t: Throwable
                    ) {

                        progressBarGuardarMaterial.visibility =
                            ProgressBar.GONE

                        buttonGuardarMaterial.isEnabled =
                            true

                        Toast.makeText(
                            this@CrearMaterialActivity,
                            "Error de conexión: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }
}