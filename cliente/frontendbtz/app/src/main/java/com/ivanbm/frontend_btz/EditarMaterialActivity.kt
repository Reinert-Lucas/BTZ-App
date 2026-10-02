package com.ivanbm.frontend_btz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ivanbm.frontend_btz.model.Material
import com.ivanbm.frontend_btz.model.MaterialResponse
import com.ivanbm.frontend_btz.model.MaterialRequest
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AlertDialog

class EditarMaterialActivity : AppCompatActivity() {

    private lateinit var editTextNombreMaterial: EditText
    private lateinit var editTextDetalleMaterial: EditText
    private lateinit var buttonGuardarMaterial: Button
    private lateinit var progressBarEditarMaterial: ProgressBar
    private lateinit var buttonEliminarMaterial: Button

    private var materialId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_material)

        editTextNombreMaterial =
            findViewById(R.id.editTextNombreMaterial)

        editTextDetalleMaterial =
            findViewById(R.id.editTextDetalleMaterial)

        buttonGuardarMaterial =
            findViewById(R.id.buttonGuardarMaterial)

        progressBarEditarMaterial =
            findViewById(R.id.progressBarEditarMaterial)

        buttonEliminarMaterial =
            findViewById(R.id.buttonEliminarMaterial)

        materialId =
            intent.getIntExtra("material_id", 0)

        if (materialId == 0) {
            Toast.makeText(
                this,
                "Material no válido",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        cargarMaterial()

        buttonGuardarMaterial.setOnClickListener {
            actualizarMaterial()
        }
        buttonEliminarMaterial.setOnClickListener {
            confirmarEliminarMaterial()
        }
    }

    private fun cargarMaterial() {

        progressBarEditarMaterial.visibility =
            ProgressBar.VISIBLE

        RetrofitClient.api
            .obtenerMaterial(materialId)
            .enqueue(
                object : Callback<MaterialResponse> {

                    override fun onResponse(
                        call: Call<MaterialResponse>,
                        response: Response<MaterialResponse>
                    ) {

                        progressBarEditarMaterial.visibility =
                            ProgressBar.GONE

                        if (!response.isSuccessful) {

                            Toast.makeText(
                                this@EditarMaterialActivity,
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

                            mostrarMaterial(
                                respuesta.data
                            )

                        } else {

                            Toast.makeText(
                                this@EditarMaterialActivity,
                                "No se pudo obtener el material",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<MaterialResponse>,
                        t: Throwable
                    ) {

                        progressBarEditarMaterial.visibility =
                            ProgressBar.GONE

                        Toast.makeText(
                            this@EditarMaterialActivity,
                            "Error de conexión: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    private fun mostrarMaterial(material: Material) {

        editTextNombreMaterial.setText(
            material.nombre
        )

        editTextDetalleMaterial.setText(
            material.detalle
        )
    }

    private fun actualizarMaterial() {

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

        progressBarEditarMaterial.visibility =
            ProgressBar.VISIBLE

        buttonGuardarMaterial.isEnabled = false

        RetrofitClient.api
            .actualizarMaterial(
                materialId,
                request
            )
            .enqueue(
                object : Callback<MaterialResponse> {

                    override fun onResponse(
                        call: Call<MaterialResponse>,
                        response: Response<MaterialResponse>
                    ) {

                        progressBarEditarMaterial.visibility =
                            ProgressBar.GONE

                        buttonGuardarMaterial.isEnabled =
                            true

                        if (!response.isSuccessful) {

                            Toast.makeText(
                                this@EditarMaterialActivity,
                                "Error HTTP ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()

                            return
                        }

                        val respuesta =
                            response.body()

                        if (
                            respuesta != null &&
                            respuesta.status
                        ) {

                            Toast.makeText(
                                this@EditarMaterialActivity,
                                "Material actualizado con éxito",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {

                            Toast.makeText(
                                this@EditarMaterialActivity,
                                "No se pudo actualizar el material",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<MaterialResponse>,
                        t: Throwable
                    ) {

                        progressBarEditarMaterial.visibility =
                            ProgressBar.GONE

                        buttonGuardarMaterial.isEnabled =
                            true

                        Toast.makeText(
                            this@EditarMaterialActivity,
                            "Error de conexión: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }
    private fun confirmarEliminarMaterial() {

        AlertDialog.Builder(this)
            .setTitle("Eliminar material")
            .setMessage(
                "¿Está seguro de que desea eliminar este material?"
            )
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarMaterial()
            }
            .show()
    }
    private fun eliminarMaterial() {

        progressBarEditarMaterial.visibility =
            ProgressBar.VISIBLE

        buttonGuardarMaterial.isEnabled = false
        buttonEliminarMaterial.isEnabled = false

        RetrofitClient.api
            .eliminarMaterial(materialId)
            .enqueue(
                object : Callback<Void> {

                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>
                    ) {

                        progressBarEditarMaterial.visibility =
                            ProgressBar.GONE

                        buttonGuardarMaterial.isEnabled =
                            true

                        buttonEliminarMaterial.isEnabled =
                            true

                        if (!response.isSuccessful) {

                            Toast.makeText(
                                this@EditarMaterialActivity,
                                "Error HTTP ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()

                            return
                        }

                        Toast.makeText(
                            this@EditarMaterialActivity,
                            "Material eliminado con éxito",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }

                    override fun onFailure(
                        call: Call<Void>,
                        t: Throwable
                    ) {

                        progressBarEditarMaterial.visibility =
                            ProgressBar.GONE

                        buttonGuardarMaterial.isEnabled =
                            true

                        buttonEliminarMaterial.isEnabled =
                            true

                        Toast.makeText(
                            this@EditarMaterialActivity,
                            "Error de conexión: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }
}