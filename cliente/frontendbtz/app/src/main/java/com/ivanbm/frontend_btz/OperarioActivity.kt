package com.ivanbm.frontend_btz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.adapter.AvisoAdapter
import com.ivanbm.frontend_btz.model.Aviso
import com.ivanbm.frontend_btz.model.TrabajosResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OperarioActivity : AppCompatActivity() {

    private lateinit var recyclerViewAvisosOperario: RecyclerView
    private lateinit var avisoAdapter: AvisoAdapter

    private var paginaAvisosActual = 1
    private var cargandoAvisos = false
    private var hayMasAvisos = true
    private var primeraCargaAvisos = true

    override fun onResume() {
        super.onResume()

        if (primeraCargaAvisos) {
            primeraCargaAvisos = false
            return
        }

        paginaAvisosActual = 1
        cargandoAvisos = false
        hayMasAvisos = true

        avisoAdapter.actualizarAvisos(emptyList())

        cargarAvisosOperario(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_operario)

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

        recyclerViewAvisosOperario =
            findViewById(R.id.recyclerViewAvisosOperario)

        configurarRecyclerView()

        cargarAvisosOperario(1)
    }

    private fun configurarRecyclerView() {

        avisoAdapter = AvisoAdapter(
            emptyList(),
            onAvisoClick = { aviso ->

                val intent = Intent(
                    this,
                    TrabajoActivity::class.java
                )

                intent.putExtra("AVISO_ID", aviso.id)

                intent.putExtra("AVISO_FECHA", aviso.fecha)
                intent.putExtra("AVISO_HORA", aviso.hora)
                intent.putExtra("AVISO_DIRECCION", aviso.direccion)
                intent.putExtra("AVISO_TELEFONO", aviso.telefono)
                intent.putExtra("AVISO_MENSAJE", aviso.mensaje)
                intent.putExtra("AVISO_OBSERVACION", aviso.observacion)
                intent.putExtra("AVISO_URGENCIA", aviso.urgencia)
                intent.putExtra("AVISO_CLIENTE", aviso.cliente.nombre)

                startActivity(intent)
            }
        )

        recyclerViewAvisosOperario.adapter = avisoAdapter

        recyclerViewAvisosOperario.layoutManager =
            LinearLayoutManager(this)

        recyclerViewAvisosOperario.addOnScrollListener(
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

                    val ultimaPosicionVisible =
                        layoutManager.findLastVisibleItemPosition()

                    val cantidadElementos =
                        avisoAdapter.itemCount

                    if (
                        ultimaPosicionVisible >= cantidadElementos - 2 &&
                        !cargandoAvisos &&
                        hayMasAvisos
                    ) {
                        cargarAvisosOperario(paginaAvisosActual + 1)
                    }
                }
            }
        )
    }

    private fun cargarAvisosOperario(pagina: Int) {

        if (cargandoAvisos || !hayMasAvisos) {
            return
        }

        cargandoAvisos = true

        RetrofitClient.api
            .obtenerTrabajosAsignados(pagina)
            .enqueue(object : Callback<com.ivanbm.frontend_btz.model.TrabajosResponse> {

                override fun onResponse(
                    call: Call<TrabajosResponse>,
                    response: Response<com.ivanbm.frontend_btz.model.TrabajosResponse>
                ) {

                    cargandoAvisos = false

                    android.util.Log.d(
                        "OPERARIO_AVISOS",
                        "HTTP: ${response.code()}"
                    )

                    if (response.isSuccessful && response.body() != null) {

                        val respuesta = response.body()!!

                        android.util.Log.d(
                            "OPERARIO_AVISOS",
                            "Respuesta: ${respuesta.data.size} avisos"
                        )

                        android.util.Log.d(
                            "OPERARIO_AVISOS",
                            "Página: ${respuesta.meta.current_page}/${respuesta.meta.last_page}"
                        )

                        if (pagina == 1) {

                            avisoAdapter.actualizarAvisos(
                                respuesta.data
                            )

                        } else {

                            avisoAdapter.agregarAvisos(
                                respuesta.data
                            )
                        }

                        paginaAvisosActual =
                            respuesta.meta.current_page

                        hayMasAvisos =
                            respuesta.meta.current_page <
                                    respuesta.meta.last_page

                    } else {

                        Toast.makeText(
                            this@OperarioActivity,
                            "Error al cargar los avisos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<TrabajosResponse>,
                    t: Throwable
                ) {

                    cargandoAvisos = false

                    Toast.makeText(
                        this@OperarioActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}