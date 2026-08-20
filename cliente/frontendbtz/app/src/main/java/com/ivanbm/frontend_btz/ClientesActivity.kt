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
import com.ivanbm.frontend_btz.adapter.ClienteAdapter
import com.ivanbm.frontend_btz.model.ClientesResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientesActivity : AppCompatActivity() {

    private lateinit var recyclerViewListaClientes: RecyclerView
    private lateinit var clienteAdapter: ClienteAdapter
    private lateinit var buttonNuevoCliente: Button
    private lateinit var progressBarCargaClientes: ProgressBar

    // Página actual
    private var paginaActual = 1

    // Última página disponible
    private var ultimaPagina = 1

    private var cargandoPagina = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_clientes)

        buttonNuevoCliente =
            findViewById(R.id.buttonNuevoCliente)

        buttonNuevoCliente.setOnClickListener {

            val intent =
                Intent(this, CrearClienteActivity::class.java)

            startActivity(intent)
        }

        recyclerViewListaClientes =
            findViewById(R.id.recyclerViewListaClientes)

        progressBarCargaClientes =
            findViewById(R.id.progressBarCargaClientes)

        clienteAdapter = ClienteAdapter(emptyList()) { cliente ->

            val intent = Intent(
                this,
                EditarClienteActivity::class.java
            )

            intent.putExtra(
                "CLIENTE_ID",
                cliente.id
            )

            startActivity(intent)
        }
        recyclerViewListaClientes.layoutManager =
            LinearLayoutManager(this)

        recyclerViewListaClientes.adapter =
            clienteAdapter

        configurarPaginacion()
    }
    //Reanuda la pagina aplicando los cambios
    override fun onResume() {
        super.onResume()
        if (!cargandoPagina) {
            paginaActual = 1
            ultimaPagina = 1

            cargarClientes(
                pagina = 1,
                reemplazarLista = true
            )
        }
    }

    private fun configurarPaginacion() {

        recyclerViewListaClientes.addOnScrollListener(
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

                    val cercaDelFinal =
                        ultimoElementoVisible >=
                                cantidadElementos - 3

                    if (
                        cercaDelFinal &&
                        !cargandoPagina &&
                        paginaActual < ultimaPagina
                    ) {

                        paginaActual++

                        cargarClientes(
                            pagina = paginaActual,
                            reemplazarLista = false
                        )
                    }
                }
            }
        )
    }

    private fun cargarClientes(
        pagina: Int,
        reemplazarLista: Boolean
    ) {

        cargandoPagina = true
        progressBarCargaClientes.visibility = View.VISIBLE

        RetrofitClient.api.obtenerClientes(pagina)
            .enqueue(object : Callback<ClientesResponse> {

                override fun onResponse(
                    call: Call<ClientesResponse>,
                    response: Response<ClientesResponse>
                ) {

                    cargandoPagina = false
                    progressBarCargaClientes.visibility = View.GONE

                    if (!response.isSuccessful) {

                        val error =
                            response.errorBody()?.string()


                        Toast.makeText(
                            this@ClientesActivity,
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

                        ultimaPagina =
                            respuesta.meta.last_page

                        if (reemplazarLista) {

                            clienteAdapter.actualizarClientes(
                                respuesta.data
                            )

                        } else {

                            clienteAdapter.agregarClientes(
                                respuesta.data
                            )
                        }

                    } else {

                        Toast.makeText(
                            this@ClientesActivity,
                            "No se pudieron obtener los clientes",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ClientesResponse>,
                    t: Throwable
                ) {

                    cargandoPagina = false
                    progressBarCargaClientes.visibility = View.GONE

                    Toast.makeText(
                        this@ClientesActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}