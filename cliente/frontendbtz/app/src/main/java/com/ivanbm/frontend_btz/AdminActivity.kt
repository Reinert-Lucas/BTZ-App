package com.ivanbm.frontend_btz

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.adapter.AvisoAdapter
import com.ivanbm.frontend_btz.model.AvisosResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {

    private lateinit var recyclerViewAvisosAdministrador: RecyclerView
    private lateinit var avisoAdapter: AvisoAdapter
    private lateinit var progressBarCargaAvisosAdministrador: ProgressBar

    private var paginaAvisosActual = 1

    private var cargandoAvisos = false

    private var hayMasAvisos = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_admin)

        recyclerViewAvisosAdministrador =
            findViewById(R.id.recyclerViewAvisosAdministrador)

        progressBarCargaAvisosAdministrador =
            findViewById(R.id.progressBarCargaAvisosAdministrador)

        avisoAdapter = AvisoAdapter(emptyList())

        recyclerViewAvisosAdministrador.layoutManager =
            LinearLayoutManager(this)

        recyclerViewAvisosAdministrador.adapter =
            avisoAdapter

        recyclerViewAvisosAdministrador.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy <= 0) {
                        return
                    }

                    val layoutManager =
                        recyclerView.layoutManager as LinearLayoutManager

                    val totalElementos =
                        layoutManager.itemCount

                    val ultimoElementoVisible =
                        layoutManager.findLastVisibleItemPosition()

                    if (
                        ultimoElementoVisible >= totalElementos - 1 &&
                        !cargandoAvisos &&
                        hayMasAvisos
                    ) {
                        cargarAvisos()
                    }
                }
            }
        )
        val buttonMenuAdministrador =
            findViewById<ImageButton>(R.id.buttonMenuAdministrador)

        buttonMenuAdministrador.setOnClickListener {
            mostrarMenuAdministrador(buttonMenuAdministrador)
        }

        cargarAvisos()
    }
    override fun onResume() {
        super.onResume()

        recargarAvisos()
    }
    private fun cargarAvisos() {

        if (cargandoAvisos || !hayMasAvisos) {
            return
        }

        cargandoAvisos = true

        progressBarCargaAvisosAdministrador.visibility =
            ProgressBar.VISIBLE

        RetrofitClient.api.obtenerAvisos(paginaAvisosActual)
            .enqueue(object : Callback<AvisosResponse> {

                override fun onResponse(
                    call: Call<AvisosResponse>,
                    response: Response<AvisosResponse>
                ) {

                    progressBarCargaAvisosAdministrador.visibility =
                        ProgressBar.GONE

                    cargandoAvisos = false

                    if (!response.isSuccessful) {

                        Toast.makeText(
                            this@AdminActivity,
                            "Error HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    val respuesta = response.body()

                    if (respuesta != null && respuesta.status) {

                        if (paginaAvisosActual == 1) {

                            avisoAdapter.actualizarAvisos(
                                respuesta.data
                            )

                        } else {

                            avisoAdapter.agregarAvisos(
                                respuesta.data
                            )
                        }

                        hayMasAvisos =
                            respuesta.meta.current_page <
                                    respuesta.meta.last_page

                        if (hayMasAvisos) {
                            paginaAvisosActual++
                        }

                    } else {

                        Toast.makeText(
                            this@AdminActivity,
                            "No se pudieron obtener los avisos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<AvisosResponse>,
                    t: Throwable
                ) {

                    progressBarCargaAvisosAdministrador.visibility =
                        ProgressBar.GONE

                    cargandoAvisos = false

                    Toast.makeText(
                        this@AdminActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
    private fun recargarAvisos() {

        paginaAvisosActual = 1
        hayMasAvisos = true
        cargandoAvisos = false

        cargarAvisos()
    }
    private fun mostrarMenuAdministrador(
        boton: ImageButton
    ) {

        val popupMenu = android.widget.PopupMenu(
            this,
            boton
        )

        popupMenu.menu.add("Crear avisos")
        popupMenu.menu.add("Gestionar herramientas y materiales")
        popupMenu.menu.add("Gestionar clientes")
        popupMenu.menu.add("Gestionar usuarios")
        popupMenu.menu.add("Cerrar sesión")

        popupMenu.setOnMenuItemClickListener { item ->

            when (item.title.toString()) {

                "Crear avisos" -> {

                    startActivity(
                        Intent(
                            this,
                            CrearAvisoActivity::class.java
                        )
                    )

                    true
                }

                "Añadir cliente" -> {

                    startActivity(
                        Intent(
                            this,
                            ClientesActivity::class.java
                        )
                    )

                    true
                }

                "Gestionar usuarios" -> {

                    startActivity(
                        Intent(
                            this,
                            PersonalActivity::class.java
                        )
                    )

                    true
                }

                else -> {
                    true
                }
            }
        }

        popupMenu.show()
    }
}