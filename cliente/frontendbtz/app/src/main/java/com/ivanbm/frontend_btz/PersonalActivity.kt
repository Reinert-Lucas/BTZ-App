package com.ivanbm.frontend_btz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.adapter.UsuarioAdapter
import com.ivanbm.frontend_btz.model.UsuariosResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.jvm.java

class PersonalActivity : AppCompatActivity() {

    private lateinit var recyclerViewListaPersonal: RecyclerView
    private lateinit var usuarioAdapter: UsuarioAdapter
    private lateinit var progressBarCargaPersonal: ProgressBar
    private lateinit var buttonNuevoUsuario: Button

    private var paginaActual = 1
    private var cargandoPagina = false
    private var ultimaPagina = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personal)

        recyclerViewListaPersonal =
            findViewById(R.id.recyclerViewListaPersonal)

        progressBarCargaPersonal =
            findViewById(R.id.progressBarCargaPersonal)

        buttonNuevoUsuario =
            findViewById(R.id.buttonNuevoUsuario)

        usuarioAdapter = UsuarioAdapter(emptyList()) { usuario ->

            val intent = Intent(
                this,
                EditarUsuarioActivity::class.java
            )

            intent.putExtra(
                "USUARIO_ID",
                usuario.id
            )

            startActivity(intent)
        }

        recyclerViewListaPersonal.layoutManager =
            LinearLayoutManager(this)

        recyclerViewListaPersonal.adapter =
            usuarioAdapter

        buttonNuevoUsuario.setOnClickListener {

            val intent = Intent(
                this,
                CrearUsuarioActivity::class.java
            )

            startActivity(intent)
        }

        configurarPaginacion()

        cargarUsuarios(1)
    }

    private fun cargarUsuarios(pagina: Int) {

        if (cargandoPagina || ultimaPagina) {
            return
        }

        cargandoPagina = true

        progressBarCargaPersonal.visibility =
            View.VISIBLE

        RetrofitClient.api.obtenerUsuarios(
            pagina
        ).enqueue(object : Callback<UsuariosResponse> {

            override fun onResponse(
                call: Call<UsuariosResponse>,
                response: Response<UsuariosResponse>
            ) {

                cargandoPagina = false

                progressBarCargaPersonal.visibility =
                    View.GONE

                if (!response.isSuccessful) {

                    Toast.makeText(
                        this@PersonalActivity,
                        "Error HTTP ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()

                    return
                }

                val respuesta = response.body()

                android.util.Log.d(
                    "PERSONAL_API",
                    "Respuesta: $respuesta"
                )

                if (respuesta != null && respuesta.status) {

                    if (pagina == 1) {

                        usuarioAdapter.actualizarUsuarios(
                            respuesta.data
                        )

                    } else {

                        val usuariosActuales =
                            obtenerUsuariosActuales()

                        usuarioAdapter.actualizarUsuarios(
                            usuariosActuales + respuesta.data
                        )
                    }

                    paginaActual = pagina

                    val paginaUltima =
                        respuesta.meta?.last_page ?: pagina

                    if (pagina >= paginaUltima) {
                        ultimaPagina = true
                    }

                } else {

                    Toast.makeText(
                        this@PersonalActivity,
                        "No se pudieron obtener los usuarios",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<UsuariosResponse>,
                t: Throwable
            ) {

                cargandoPagina = false

                progressBarCargaPersonal.visibility =
                    View.GONE

                android.util.Log.e(
                    "PERSONAL_API",
                    "Error de conexión",
                    t
                )

                Toast.makeText(
                    this@PersonalActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun configurarPaginacion() {

        recyclerViewListaPersonal.addOnScrollListener(
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

                    if (dy <= 0) {
                        return
                    }

                    val layoutManager =
                        recyclerView.layoutManager
                                as LinearLayoutManager

                    val cantidadElementos =
                        layoutManager.itemCount

                    val ultimoElementoVisible =
                        layoutManager.findLastVisibleItemPosition()

                    if (
                        ultimoElementoVisible >=
                        cantidadElementos - 3
                    ) {

                        cargarUsuarios(
                            paginaActual + 1
                        )
                    }
                }
            }
        )
    }

    private fun obtenerUsuariosActuales(): List<com.ivanbm.frontend_btz.model.Usuario> {

        val cantidad =
            usuarioAdapter.itemCount

        return (0 until cantidad).mapNotNull { posicion ->

            usuarioAdapter.obtenerUsuario(posicion)
        }
    }

    override fun onResume() {
        super.onResume()

        paginaActual = 1
        ultimaPagina = false

        cargarUsuarios(1)
    }
}