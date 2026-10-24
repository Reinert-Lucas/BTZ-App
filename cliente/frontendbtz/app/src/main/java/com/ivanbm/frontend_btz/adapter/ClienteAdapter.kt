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

        if (cliente.asegurado) {
            holder.textViewAseguradoCliente.text = "Asegurado"
            holder.textViewAseguradoCliente.setBackgroundResource(
                R.drawable.bg_cliente_asegurado
            )
            holder.textViewAseguradoCliente.setTextColor(
                android.graphics.Color.parseColor("#247342")
            )

            val detalle = cliente.asegurado_detalle
                ?.trim()
                .orEmpty()

            if (detalle.isNotEmpty()) {
                holder.textViewDetalleAseguradoCliente.text =
                    "Detalle del seguro: $detalle"

                holder.textViewDetalleAseguradoCliente.visibility =
                    View.VISIBLE

                holder.textViewDetalleAseguradoCliente.setBackgroundResource(
                    R.drawable.bg_detalle_asegurado_cliente
                )

                holder.textViewDetalleAseguradoCliente.setPadding(
                    10, 8, 10, 8
                )
            } else {
                holder.textViewDetalleAseguradoCliente.visibility =
                    View.GONE
            }

        } else {
            holder.textViewAseguradoCliente.text = "No asegurado"
            holder.textViewAseguradoCliente.setBackgroundResource(
                R.drawable.bg_cliente_no_asegurado
            )
            holder.textViewAseguradoCliente.setTextColor(
                android.graphics.Color.parseColor("#686779")
            )

            holder.textViewDetalleAseguradoCliente.visibility =
                View.GONE
        }

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