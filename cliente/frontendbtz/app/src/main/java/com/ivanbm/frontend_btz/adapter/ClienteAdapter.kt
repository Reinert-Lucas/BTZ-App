package com.ivanbm.frontend_btz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.R
import com.ivanbm.frontend_btz.model.Cliente

class ClienteAdapter(
    private var clientes: List<Cliente>,
    private val onClienteClick: (Cliente) -> Unit
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textViewNombreCliente: TextView =
            itemView.findViewById(R.id.textViewNombreCliente)

        val textViewEmailCliente: TextView =
            itemView.findViewById(R.id.textViewEmailCliente)

        val textViewAseguradoCliente: TextView =
            itemView.findViewById(R.id.textViewAseguradoCliente)

        val textViewDetalleAseguradoCliente: TextView =
            itemView.findViewById(R.id.textViewDetalleAseguradoCliente)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClienteViewHolder {

        val vistaCliente = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)

        return ClienteViewHolder(vistaCliente)
    }

    override fun onBindViewHolder(
        holder: ClienteViewHolder,
        position: Int
    ) {

        val cliente = clientes[position]
        holder.textViewNombreCliente.text = cliente.nombre
        holder.textViewEmailCliente.text = cliente.email

        holder.textViewAseguradoCliente.text = if (cliente.asegurado) {
            "Asegurado: Sí"
        } else {
            "Asegurado: No"
        }

        holder.textViewDetalleAseguradoCliente.text =
            "Detalle: ${cliente.asegurado_detalle ?: "-"}"
        holder.itemView.setOnClickListener {
            onClienteClick(cliente)
        }
    }

    override fun getItemCount(): Int {
        return clientes.size
    }

    fun actualizarClientes(nuevosClientes: List<Cliente>) {
        clientes = nuevosClientes
        notifyDataSetChanged()
    }
    fun agregarClientes(nuevosClientes: List<Cliente>) {
        clientes = clientes + nuevosClientes
        notifyDataSetChanged()
    }
}