package com.ivanbm.frontend_btz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.adapter.MaterialAdapter
import com.ivanbm.frontend_btz.model.MaterialesResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.jvm.java

class MaterialActivity : AppCompatActivity() {

    private lateinit var recyclerViewMateriales: RecyclerView
    private lateinit var materialAdapter: MaterialAdapter
    private lateinit var progressBarCargaMateriales: ProgressBar
    private lateinit var buttonCrearMaterial: Button

    private var paginaMaterialesActual = 1
    private var cargandoMateriales = false
    private var hayMasMateriales = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_material)

        recyclerViewMateriales =
            findViewById(R.id.recyclerViewMateriales)

        progressBarCargaMateriales =
            findViewById(R.id.progressBarCargaMateriales)

        buttonCrearMaterial =
            findViewById(R.id.buttonCrearMaterial)

        materialAdapter = MaterialAdapter(emptyList()) { material ->

            val intent = Intent(
                this,
                EditarMaterialActivity::class.java
            )

            intent.putExtra(
                "material_id",
                material.id
            )

            startActivity(intent)
        }

        recyclerViewMateriales.layoutManager =
            LinearLayoutManager(this)

        recyclerViewMateriales.adapter =
            materialAdapter

        buttonCrearMaterial.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CrearMaterialActivity::class.java
                )
            )
        }

        recyclerViewMateriales.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(
                        recyclerView,
                        dx,
                        dy
                    )

                    if (dy <= 0) return

                    val layoutManager =
                        recyclerView.layoutManager
                                as LinearLayoutManager

                    val totalElementos =
                        layoutManager.itemCount

                    val ultimoElementoVisible =
                        layoutManager.findLastVisibleItemPosition()

                    if (
                        ultimoElementoVisible >= totalElementos - 1 &&
                        !cargandoMateriales &&
                        hayMasMateriales
                    ) {
                        cargarMateriales()
                    }
                }
            }
        )

        cargarMateriales()
    }

    override fun onResume() {
        super.onResume()

        recargarMateriales()
    }

    private fun recargarMateriales() {

        paginaMaterialesActual = 1
        hayMasMateriales = true
        cargandoMateriales = false

        cargarMateriales()
    }

    private fun cargarMateriales() {

        if (cargandoMateriales || !hayMasMateriales) {
            return
        }

        cargandoMateriales = true

        progressBarCargaMateriales.visibility =
            ProgressBar.VISIBLE

        RetrofitClient.api
            .obtenerMateriales(paginaMaterialesActual)
            .enqueue(
                object : Callback<MaterialesResponse> {

                    override fun onResponse(
                        call: Call<MaterialesResponse>,
                        response: Response<MaterialesResponse>
                    ) {

                        progressBarCargaMateriales.visibility =
                            ProgressBar.GONE

                        cargandoMateriales = false

                        if (!response.isSuccessful) {

                            Toast.makeText(
                                this@MaterialActivity,
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

                            if (
                                paginaMaterialesActual == 1
                            ) {

                                materialAdapter
                                    .actualizarMateriales(
                                        respuesta.data
                                    )

                            } else {

                                materialAdapter
                                    .agregarMateriales(
                                        respuesta.data
                                    )
                            }

                            hayMasMateriales =
                                respuesta.meta.current_page <
                                        respuesta.meta.last_page

                            if (hayMasMateriales) {
                                paginaMaterialesActual++
                            }

                        } else {

                            Toast.makeText(
                                this@MaterialActivity,
                                "No se pudieron obtener los materiales",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<MaterialesResponse>,
                        t: Throwable
                    ) {

                        progressBarCargaMateriales.visibility =
                            ProgressBar.GONE

                        cargandoMateriales = false

                        Toast.makeText(
                            this@MaterialActivity,
                            "Error de conexión: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }
}